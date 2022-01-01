package donghyeon.dev.concurrency.terminate.invokeall;

/**
 * # 단번에 실행하는 서비스 
 * 
 * 일련의 작업을 순서대로 처리해야 하며, 
 * 작업이 모두 끝나기 전에는 리턴되지 않는 메서드를 생각해 보자.
 * 이런 메서드는 내부에서만 사용할 Executor 인스턴스를 하나 확보 할 수 있다면
 * 서비스의 시작과 종료를 쉽게 관리할 수 있다.
 */
