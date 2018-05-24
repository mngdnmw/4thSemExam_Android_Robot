package mafioso.so.so.android_robot.bll;

public class DebugLogger {
        private String debug;
        private ChangeListener listener;

        public String getDebug() {
            return debug;
        }

        public void setDebug(String debug) {
            this.debug = debug;
            if (listener != null) listener.onChange();
        }

        public ChangeListener getListener() {
            return listener;
        }

        public void setListener(ChangeListener listener) {
            this.listener = listener;
        }

        public interface ChangeListener {
            void onChange();
        }
    }

