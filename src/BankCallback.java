import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public interface BankCallback extends java.rmi.Remote {
    void onBalanceUpdate(String accountNumber, double newBalance) throws RemoteException;
}

