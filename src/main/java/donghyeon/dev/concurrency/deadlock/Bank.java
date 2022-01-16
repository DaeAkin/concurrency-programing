package donghyeon.dev.concurrency.deadlock;

public class Bank {
    /**
     * 동적인 락 순서에 의한 데드락. 이런 코드는 금물.
     * 데드락 위험이 있다.
     * <p>
     * X 계좌 -> Y 계좌
     * Y 계좌 -> X 계좌
     * 같은 상황에서 데드락이 발생한다.
     *
     * @param fromAccount
     * @param toAccount
     * @param amount
     */
    public void transferMoney(Account fromAccount,
                              Account toAccount,
                              DollarAmount amount) {
        synchronized (fromAccount) {
            synchronized (toAccount) {
                if (fromAccount.getBalance().compareTo(amount) < 0)
                    throw new InsufficientFundsException();
                else {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                }
            }
        }
    }

    private static final Object tieLock = new Object();
    /**
     * 락을 확보하려는 순서를 내부적으로 제어할 수 없기 때문에
     * 여기에서 데드락을 방지하려면 락을 특정 순서에 맞춰 확보하도록 해야하고,
     * 락을 확보하는 순서를 프로그램 전반적으로 동일하게 적용해야 한다.
     * 객체에 순서를 부여할 수 있는 방법 중 하나는 바로 System.identifyHashCode를 사용하는 방법인데,
     * identifyHashCode 메서드는 해당 객체의 Object.hashCode 메서드를 호출했을 때의 값을 알려준다.
     * <p>
     * 거의 발생하지 않는 일이지만 두개의 객체가 같은 hashCode 값을 갖고 있는 경우에는
     * 또 다른 방법을 사용해 락 확보 순서를 조절해야 하며,
     * 그렇지 않은 경우에는 역시 데드락이 발생할 가능성이 있다.
     * 이와 같은 경우에 락 순서가 일정하지 않을 수 있다는 문제점을 제거하려면
     * 세 번째 타이 브레이킹(tie-breaking) 락을 사용하는 방법이 있다.
     *
     * X -> Y을 예시로 X의 해시코드가 더 높다고 가정할 경우 
     * 
     * X -> Y , Y -> X 둘다 락 잡는 순서가 같아지게 된다. 
     * 
     * Account 클래스 내부에 계좌 번호와 같이 유일하면서 불변이고 
     * 비교도 가능한 값을 키로 갖고 있다면 한결 쉬운 방법으로 락 순서를 지정할 수 있다.
     * Account 객체를 그 내부의 키를 기준으로 정렬한 다음 
     * 정렬한 순서대로 락을 확보한다면 타이 브레이킹 방법을 사용하지 않고도
     * 전체 프로그램을 통틀어 계좌를 사용할 때 락이 걸리는 순서를 일정하게 유지할 수 있다.
     * 
     * @param fromAccount
     * @param toAccount
     * @param amount
     */
    public void notDeadlockTransferMoney(Account fromAccount,
                                         Account toAccount,
                                         DollarAmount amount) {

        class Helper {
            public void transfer() {
                if (fromAccount.getBalance().compareTo(amount) < 0)
                    throw new InsufficientFundsException();
                else {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                }
            }
        }
        int fromHash = System.identityHashCode(fromAccount);
        int toHash = System.identityHashCode(toAccount);
        
        if (fromHash < toHash) {
            synchronized (fromAccount) {
                synchronized (toAccount) {
                    new Helper().transfer();
                }
            }
        } else if (fromHash > toHash) {
            synchronized (toAccount) {
                synchronized (fromAccount) {
                    new Helper().transfer();
                }
            }
        } else {
            synchronized (tieLock) {
                synchronized (fromAccount) {
                    synchronized (toAccount) {
                        new Helper().transfer();
                    }
                }
            }
        }


    }

    static class Account {
        public DollarAmount getBalance() {
            return new DollarAmount();
        }

        public void debit(DollarAmount amount) {

        }

        public void credit(DollarAmount amount) {

        }
    }

    static class DollarAmount implements Comparable<DollarAmount> {

        @Override
        public int compareTo(DollarAmount o) {
            return 0;
        }
    }

    static class InsufficientFundsException extends RuntimeException {

    }
}
