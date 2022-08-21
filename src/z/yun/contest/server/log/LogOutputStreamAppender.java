package z.yun.contest.server.log;

import ch.qos.logback.core.OutputStreamAppender;

import java.io.FilterOutputStream;
import java.io.OutputStream;

public class LogOutputStreamAppender<E> extends OutputStreamAppender<E> {
    private static final DelegatingOutputStream DELEGATING_OUTPUT_STREAM = new DelegatingOutputStream(null);

    @Override
    public void start() {
        setOutputStream(DELEGATING_OUTPUT_STREAM);
        super.start();
    }

    public static void setStaticOutputStream(OutputStream outputStream) {
        DELEGATING_OUTPUT_STREAM.setOutputStream(outputStream);
    }

    private static class DelegatingOutputStream extends FilterOutputStream {

        public DelegatingOutputStream(OutputStream out) {
            super(new OutputStream() {
                @Override
                public void write(int b) {
                }
            });
        }

        void setOutputStream(OutputStream outputStream) {
            this.out = outputStream;
        }
    }

}