/*
 * $Id$
 * 
 * Copyright (c) 2016, Simsilica, LLC
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the 
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package example;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.slf4j.*;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.ClientStateListener.DisconnectInfo;
import com.jme3.network.ErrorListener;

import com.simsilica.lemur.Action;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.OptionPanelState;

import example.net.client.GameClient;

/**
 *  Manages the connection and game client when connected to a server.
 *
 *  @author    Paul Speed
 */
public class ConnectState extends BaseAppState {

    static Logger log = LoggerFactory.getLogger(ConnectState.class);

    private String host;
    private int port;
    
    private GameClient client;
    private ConnectionObserver connectionObserver = new ConnectionObserver();
    private Connector connector;
    private Thread renderThread;
 
    private boolean closing;
    
    public ConnectState( String host, int port ) {
        this.host = host;
        this.port = port;
    }

    @Override   
    protected void initialize( Application app ) {
        this.renderThread = Thread.currentThread();
        connector = new Connector();
        connector.start();
    }
 
    @Override   
    protected void cleanup( Application app ) {
        closing = true;
        if( client != null ) {
            client.close();
        }
        
        // And re-enable the main menu
        getState(MainMenuState.class).setEnabled(true);
    }
    
    @Override   
    protected void onEnable() {
    }
    
    @Override   
    protected void onDisable() {
    }
    
    protected boolean isRenderThread() {
        return Thread.currentThread() == renderThread;
    }

    protected void showError( final String title, final Throwable e, final boolean fatal ) {
        showError(title, null, e, fatal);
    }
    
    protected void showError( final String title, final String message, final Throwable e, final boolean fatal ) {
        if( isRenderThread() ) {
            String m = message;
            if( e != null ) {
                if( m != null ) {
                    m += "\n";
                } else {
                    m = "";
                }
                m += e.getClass().getSimpleName() + ":" + e.getMessage();
            }
            getState(OptionPanelState.class).show(title, m, new ExitAction(fatal));    
        } else {
            getApplication().enqueue(new Callable() {
                    public Object call() {
                        showError(title, e, fatal);
                        return null;
                    }
                });
        }
    }
    
    protected void setClient( final GameClient client ) {
        if( isRenderThread() ) {
            this.client = client;
        } else {
            getApplication().enqueue(new Callable() {
                    public Object call() {
                        setClient(client);
                        return null;
                    }
                });
        }
    }

    protected void connected() {
        log.info("connected()");
    }
    
    protected void disconnected( DisconnectInfo info ) {
        log.info("disconnected(" + info + ")");
        if( closing ) {
            return;
        }        
        if( info != null ) {
            showError("Disconnect", info.reason, info.error, true);
        } else {
            showError("Disconnected", "Unknown error", null, true);
        }
        
    }

    private class ExitAction extends Action {
        private boolean close;
        
        public ExitAction( boolean close ) {
            super("Ok");
            this.close = close;
        }
 
        public void execute( Button source ) {
            if( close ) {
                log.info("Detaching ConnectionState");
                getStateManager().detach(ConnectState.this);
            }
        }                    
    }
    
    private class ConnectionObserver implements ClientStateListener, ErrorListener<Client> {
        public void clientConnected( final Client c ) {
            log.trace("clientConnected(" + c + ")");
            getApplication().enqueue(new Callable() {
                    public Object call() {
                        connected();
                        return null;
                    }
                });
        }
 
        public void clientDisconnected( final Client c, final DisconnectInfo info ) {
            log.trace("clientDisconnected(" + c + ", " + info + ")");        
            getApplication().enqueue(new Callable() {
                    public Object call() {
                        disconnected(info);
                        return null;
                    }
                });
        }
 
        public void handleError( Client source, Throwable t ) {
            log.error("Connection error", t);
            showError("Connection Error", t, true);
        }           
    }

    private class Connector extends Thread {
        
        public Connector() {            
        }
        
        public void run() {
            
            try {
                log.info("Creating game client for:" + host + " " + port);            
                GameClient client = new GameClient(host, port);
                setClient(client);
                client.getClient().addClientStateListener(connectionObserver);
                client.getClient().addErrorListener(connectionObserver);
                
                log.info("Starting client...");
                client.start();                
                log.info("Client started.");
            } catch( IOException e ) {
                showError("Error Connecting", e, true);
            }
        }
    }    
}

