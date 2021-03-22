/*
===============================================================
ClientBoundaryWebsockArilAsynch.java
Use the aril language and the support specified in the
configuration file IssProtocolConfig.txt

The business logic is defined in RobotControllerArilBoundary
that is 'message-driven'
===============================================================
*/
package it.unibo.wenv;
import it.unibo.annotations.ArilRobotSpec;
import it.unibo.consolegui.ConsoleGui;
import it.unibo.interaction.IssOperations;
import it.unibo.supports.IssCommSupport;
import it.unibo.supports.RobotApplicationStarter;

@ArilRobotSpec
public class ClientResumableBoundaryWebsockArilAsynch {
    private RobotInputController controller;
    private ActorRobotObserver actorObs = new ActorRobotObserver();
    private ConsoleGui console;
    private static Boolean init = false;
    //Constructor
    public ClientResumableBoundaryWebsockArilAsynch(IssOperations rs){
        IssCommSupport rsComm = (IssCommSupport)rs;
        controller = new RobotInputController(rsComm, true, true );
        rsComm.registerObserver( controller );
        rsComm.registerObserver( actorObs );
        console =  new ConsoleGui(controller);
        System.out.println("ClientBoundaryWebsockBasicAsynch | CREATED with rsComm=" + rsComm);
    }

    public String doBoundary() throws InterruptedException {
        System.out.println("ClientBoundaryWebsockBasicAsynch | doBoundary " + controller  );
        String result = controller.doBoundary();
        actorObs.close();
        return result;
    }


    public static void main(String args[]){
        try {
            System.out.println("ClientBoundaryWebsockBasicAsynch | main start n_Threads=" + Thread.activeCount());
            Object appl = RobotApplicationStarter.createInstance(ClientResumableBoundaryWebsockArilAsynch.class);
            System.out.println("ClientBoundaryWebsockBasicSynch  | appl n_Threads=" + Thread.activeCount());
            String trip = ((ClientResumableBoundaryWebsockArilAsynch)appl).doBoundary();
            System.out.println("ClientBoundaryWebsockBasicAsynch | trip="   );
            System.out.println( trip  );
            System.out.println("ClientBoundaryWebsockBasicAsynch | main end n_Threads=" + Thread.activeCount());
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
}
