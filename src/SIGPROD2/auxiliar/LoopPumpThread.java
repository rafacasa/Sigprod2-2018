/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sigprod2.auxiliar;

import sigprod2.modelo.Rede;

/**
 *
 * @author Rafael Casa
 */
public class LoopPumpThread extends Thread{
    private Rede rede;

    public LoopPumpThread(Rede rede) {
        this.rede = rede;
    }
    
    @Override
    public void run() {
        this.rede.loopPump();
    }
}
