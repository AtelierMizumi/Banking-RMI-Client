import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BankCallback extends Remote {
    void notifyTransfer(String message) throws RemoteException;
}

