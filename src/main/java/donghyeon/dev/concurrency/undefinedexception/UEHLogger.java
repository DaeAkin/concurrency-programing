package donghyeon.dev.concurrency.undefinedexception;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * 예외 내용을 로그 파일에 출력하는 UncaughtExceptionHandler
 * 
 * 예상치 못한 예외 상황이 발생했을 때 JVM은 먼저 
 * 스레드별로 지정된 핸들러가 있는지를 확인하고,
 * 그 다음에야 ThreadGroup에 설정된 내용이 있는지 살펴본다.
 * ThreadGroup에 정의되어 있는 기본적인 처리 방법은
 * 먼저 상위 ThreadGroup에게 처리 기회를 넘기는 일이고,
 * 가장 최상위 ThreadGroup까지 올라가는 과정에서 핸들러가 지정된 경우가 있는지를 확인하게 된다.
 * 최상위 ThreadGroup에서는 기본 시스템 핸들러에 처리를 넘기거나,
 * 지정된 기본 시스템 핸들러가 없다면 그냥 콘솔에 스택 트레이스를 출력해버린다.
 * 
 * 잠깐 실행하고 마는 애플리케이션이 아닌 이상, 예외가 발생했을 때
 * 로그 파일에 오류를 출력하는 간단한 기능만이라도 확보할 수 있도록
 * 모든 스레드를 대상으로 UncaughtExceptionHandler를 활용해야 한다.
 */
public class UEHLogger implements Thread.UncaughtExceptionHandler{
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Logger logger = Logger.getAnonymousLogger();
        logger.log(Level.SEVERE,
                "Thread terminated with exception: " + t.getName(),
                e);
    }
}
