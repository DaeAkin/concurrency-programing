package donghyeon.dev.concurrency.deadlock;

import net.jcip.annotations.GuardedBy;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class TaxiExample {
    class Taxi { 
        @GuardedBy("this")
        private Point location, destination;
        private final Dispatch
    }
    
    class Dispatcher {
        @GuardedBy("this")
        private final Set<Taxi> taxis;
        @GuardedBy("this")
        private final Set<Taxi> availableTaxis;

        public Dispatcher() {
            taxis = new HashSet<>();
            availableTaxis = new HashSet<>();
        }
    }
}
