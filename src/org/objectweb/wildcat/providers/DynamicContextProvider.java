/*
 * Copyright (c) 2004-2005 Universite de Nantes (LINA)
 * Copyright (c) 2005-2006 France Telecom
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact: Pierre-Charles David <pcdavid@gmail.com>
 */
package org.objectweb.wildcat.providers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.objectweb.wildcat.Context;
import org.objectweb.wildcat.Path;
import org.objectweb.wildcat.dependencies.ExpressionDependenciesCollector;
import org.objectweb.wildcat.events.PathEvent;
import org.objectweb.wildcat.expressions.EvaluationException;
import org.objectweb.wildcat.expressions.Expression;
import org.objectweb.wildcat.expressions.Interpreter;

/**
 * This provider extends {@link BasicContextProvider} and adds support for:
 * <ol>
 * <li><em>automatic attributes</em>, which get their value from external sensors
 * queried at regular interval</li>
 * <li><em>synthetic attributes</em>, which are defined in terms of other attributes
 * in the context by an expression</li>
 * </ol>
 * 
 * @author Pierre-Charles David <pcdavid@gmail.com>
 */
public class DynamicContextProvider extends BasicContextProvider {
    // Automatic attributes: updated by data from sensors
    protected Map<Path, AttributesUpdater> updaters;
    protected ScheduledExecutorService scheduler;
    // Synthetic attributes: defined by expressions with dependencies
    protected Interpreter inter;
    protected Map<Path, Expression> definitions;

    public DynamicContextProvider(Interpreter inter) {
        this(Executors.newScheduledThreadPool(1), inter);
    }

    public DynamicContextProvider(ScheduledExecutorService scheduler, Interpreter inter) {
        this.scheduler = scheduler;
        this.inter = inter;
        this.updaters = new HashMap<Path, AttributesUpdater>();
        this.definitions = new HashMap<Path, Expression>();
    }

    public synchronized void attachSensor(Path path, Sampler sampler, long delay,
            TimeUnit unit) {
        if (updaters.containsKey(path)) {
            throw new IllegalArgumentException("Path " + path
                    + " already has a sensor attached.");
        } else {
            updaters.put(path, new AttributesUpdater(path, sampler, delay, unit));
        }
    }

    public synchronized void detachSensor(Path path) {
        if (updaters.containsKey(path)) {
            stopSensor(path);
            updaters.remove(path);
        } else {
            throw new IllegalArgumentException("No sensor at " + path + ".");
        }
    }

    public synchronized void startSensor(final Path path) {
        if (updaters.containsKey(path)) {
            updaters.get(path).start();
        } else {
            throw new IllegalArgumentException("No sensor at " + path + ".");
        }
    }

    public synchronized void stopSensor(Path path) {
        if (updaters.containsKey(path)) {
            updaters.get(path).stop();
        } else {
            throw new IllegalArgumentException("No sensor at " + path + ".");
        }
    }

    public synchronized void setDefinition(Path attribute, Expression def) {
        if (attribute == null || attribute.isPattern() || attribute.isAbsolute()
                || attribute.isResource()) {
            throw new IllegalArgumentException();
        }
        Expression oldDef = definitions.get(attribute);
        Expression expr = def;
        definitions.put(attribute, expr);
        declareDependencies(attribute, expr);
        if (oldDef != null) {
            retractDependencies(attribute, oldDef); // FIXME handle common dependencies
        }
        update(attribute, null);
    }

    public synchronized void setDefinition(Path attribute, String def) {
        setDefinition(attribute, inter.parse(def));
    }

    public synchronized void setDefinition(String attribute, String def) {
        setDefinition(Context.createPath(attribute), inter.parse(def));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.wildcat.dependencies.Updatable#update(org.objectweb.wildcat.Path,
     *      org.objectweb.wildcat.events.PathEvent)
     */
    public synchronized void update(Path path, PathEvent cause) {
        Expression def = definitions.get(path);
        if (def != null) {
            try {
                setValue(path, inter.evaluate(def));
            } catch (EvaluationException e) {
                // TODO Log the error, but otherwise ignore it.
                // It's OK for a definition to be invalid (maybe temporarily).
            }
        } else {
            throw new IllegalArgumentException("Path " + path
                    + " is not a synthetic attribute.");
        }
    }

    private void declareDependencies(Path attr, Expression expr) {
        for (Path dep : getDependencies(expr)) {
            dependencyGraph.addDependency(this.getPath().append(attr), dep);
        }
    }

    private void retractDependencies(Path attr, Expression expr) {
        for (Path dep : getDependencies(expr)) {
            dependencyGraph.removeDependency(this.getPath().append(attr), dep);
        }
    }

    private Collection<Path> getDependencies(Expression expr) {
        ExpressionDependenciesCollector collector = new ExpressionDependenciesCollector();
        expr.accept(collector);
        return collector.getDependencies();
    }

    /**
     * Represents a task responsible for
     * 
     * @author Pierre-Charles David <pcdavid@gmail.com>
     */
    private class AttributesUpdater implements Runnable {
        private Path path;
        private Sampler sampler;
        private long period;
        private TimeUnit unit;
        private ScheduledFuture future;

        public AttributesUpdater(Path path, Sampler sampler, long period, TimeUnit unit) {
            this.path = path;
            this.sampler = sampler;
            this.period = period;
            this.unit = unit;
        }

        public synchronized void start() {
            if (future != null) {
                throw new IllegalStateException("Sensor at " + path + " already running.");
            } else {
                future = scheduler.scheduleAtFixedRate(this, 0, period, unit);
            }
        }

        public synchronized void stop() {
            if (future == null) {
                throw new IllegalStateException("Sensor at " + path + " already stopped.");
            } else {
                future.cancel(false);
                future = null;
            }
        }

        public void run() {
            Map<String, Object> samples = sampler.sample();
            Resource target = findResource(path, false);
            target.setAttributesValues(samples);
        }
    }
}
