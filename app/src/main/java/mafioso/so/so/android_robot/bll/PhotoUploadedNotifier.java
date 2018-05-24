package mafioso.so.so.android_robot.bll;

public class PhotoUploadedNotifier {
        private boolean uploaded = false;
        private ChangeListener listener;

        public boolean isUploaded() {
            return uploaded;
        }

        public void setUploaded(boolean boo) {
            this.uploaded = boo;
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

