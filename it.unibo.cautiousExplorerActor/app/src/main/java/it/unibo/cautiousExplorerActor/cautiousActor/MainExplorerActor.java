package it.unibo.cautiousExplorerActor.cautiousActor;
import it.unibo.supports2021.IssWsHttpJavaSupport;

public class MainExplorerActor {

    public MainExplorerActor(){
        IssWsHttpJavaSupport support = IssWsHttpJavaSupport.createForWs("localhost:8091" );

        CautiousExplorerActor explorer = new CautiousExplorerActor("CautiousExplorer",support);
        support.registerActor(explorer);
        explorer.send("startApp");
        System.out.println("MainRobotActorMainExplorerActorJava | CREATED  n_Threads=" + Thread.activeCount());

    }

    public static void main(String args[]){
        try{
            System.out.println("MainExplorerActor | main start n_Threads=" + Thread.activeCount());
            new MainExplorerActor();
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }

}
