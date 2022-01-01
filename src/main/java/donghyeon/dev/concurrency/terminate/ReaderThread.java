package donghyeon.dev.concurrency.terminate;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * interrupt 메서드를 오버라이드해 표준에 정의되어 있지 않은 작업 중단 방법을 구현
 * 이렇게 구현하면 ReaderThread 클래스에 인터럽트를걸었을 때
 * read 메서드에서 대기 중인 상태이거나 기타 인터럽트에 응답할 수 있는
 * 블러킹 메서드에 멈춰 있을 때에도 작업을 중단할 수 있다.
 */
public class ReaderThread extends Thread{
    private final Socket socket;
    private final InputStream in;

    public ReaderThread(Socket socket, InputStream in) {
        this.socket = socket;
        this.in = in;
    }
    
    @Override
    public void interrupt() {
        try {
            socket.close();
        } catch (IOException ignored) {
            
        } finally {
            super.interrupt();
        }
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[1000];
            while (true) {
                int count = in.read(buf);
                if (count < 0) 
                    break;
                else if (count > 0)
                    processBuffer(buf,count);
                    
            }
        } catch (IOException e) {
            /**
             *  스레드를 종료한다.
             */
        }
    }
    
    public void processBuffer(byte[] buf, int count) {
        
    }
}
