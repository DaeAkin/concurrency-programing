package donghyeon.dev.concurrency.unnormalterminate;

/**
 * 
 * # 정의되지 않은 예외 처리
 * 
 * 스레드 API를 보면 UncaughtExceptionHandler 라는 기능을 제공하는데,
 * 이 기능을 사용하면 처리하지 못한 예외 상황으로 인해 
 * 특정 스레드가 종료되는 시점을 정확히 알 수 있다.
 * 
 * <code>UncaughtExceptionHandler</code>
 * 
 * 처리하지 못한 예외 상황 때문에 스레드가 종료되는 경우에 
 * JVM이 애플리케이션에서 정의한 UncaughtExceptionHandler를 호출하도록 할 수 있다.
 * 만약 핸들러가 하나도 정의되어 있지 않다면
 * 기본 동작으로 스택 트레이스를 콘솔을 System.err 스트림에 출력한다.
 */
