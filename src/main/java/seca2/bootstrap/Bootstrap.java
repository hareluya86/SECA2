/**
 * This is the most important class in the entire application!!! It is a 
 * core that dispatches, load and manage key components of the application.
 * <ul>
 * <li>User Management</li>
 * <li>Program Management</li>
 * <li>Presentation Management</li>
 * </ul>
 * It is important that these parts operate independently of each other and they
 * can be changed/enhanced without having to change the others.
 * 
 */

package seca2.bootstrap;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLActions;
import com.ocpsoft.pretty.faces.annotation.URLBeanName;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.ProgramModule;
import seca2.bootstrap.module.UserModule;

/**
 *
 * @author vincent.a.lee
 */
@URLMappings(mappings={
    @URLMapping(id="home", pattern="/",viewId="/program/index.xhtml"),
    @URLMapping(id="program", pattern="/program/#{bootstrap.program}/",viewId="/program/index.xhtml"),
    @URLMapping(id="install", pattern="/install/",viewId="/program/programs/install/install.xhtml")
})
@URLBeanName("bootstrap")
@Named("bootstrap")
@SessionScoped
public class Bootstrap implements Serializable {
    
    /**
     * Use this for anything. Anything!
     */
    private Map<String,Object> elements;
    
    /**
     * Injected view parameters
     */
    private String program; //which program are you loading
    
    @PostConstruct
    public void init(){
        //Initialize all parameters
        elements = new HashMap<String,Object>();
        
        //User Management
        elements.put("user-module-login-block", "/programs/user/login_block.xhtml");
        
        //Program Management
        
        //Template Management
        
        //Error Management
        elements.put("error-module-block", "/programs/error/errorBox.xhtml");
    }
    
    /**
     * User Management!
     * <p>
     * This is the part where you manage authentication and sessions.
     * <p>
     * A bootstrap class shouldn't have a dependency on a form class. Bootstrap
     * exists even before any forms are called, but what the hack...before I 
     * figure out how to split this entire bootstrap class into Servlet Filters,
     * let's just put it here first...
     * 
     */
    @Inject private UserModule userModule;
    
    @URLActions(actions={
        @URLAction(mappingId="home", onPostback=true),
        @URLAction(mappingId="program", onPostback=true)
    })
    public void checkLogin(){
        boolean loggedIn = userModule.checkSessionActive();
        
        //Put it inside elements
        this.elements.put("user", loggedIn);
    }
    
    /**
     * Program Management!
     * <p>
     * This is the part where you manage what to show depending on what the user 
     * has inputted in the request URL.
     * <p>
     * 
     */
    @Inject private ProgramModule programModule;
    @URLActions(actions={
        @URLAction(mappingId="home", onPostback=false),
        @URLAction(mappingId="program", onPostback=false)
    })
    public void loadProgram(){
        //Let's just get the template up first
        //It's damn ugly coding but no choice....
        if(this.program == null || this.program.isEmpty()){
            programModule.setCurrentProgramIndex(ProgramModule.DEFAULT_PROGRAM);
            program = programModule.getProgramNames().get(programModule.getCurrentProgramIndex());
        }else{
            programModule.setCurrentProgramIndex(-1);
            for(int i=0; i<programModule.getPrograms().size(); i++){
                String prog = programModule.getPrograms().get(i).getPROGRAM_NAME();
                if(this.program.equalsIgnoreCase(prog)){
                    programModule.setCurrentProgramIndex(i);
                }
            }
            if(programModule.getCurrentProgramIndex() < 0)
                throw new RuntimeException("Cannot find program "+this.program);
        }
        this.elements.put("program", programModule.getCurrentProgram());
        this.elements.put("program-location", "/programs/"+program+"/layout.xhtml"); //I have to use this first...
    }
    
    /**
     * Presentation Management!
     * <p>
     * This is the part where you manage how to present each page nicely.
     */
    @URLActions(actions={
        @URLAction(mappingId="home", onPostback=false),
        @URLAction(mappingId="program", onPostback=false)
    })
    public void loadPresentation(){
        //shortcut for the time being
        this.elements.put("template-location", "/templates/mytemplate/template-layout.xhtml");
    }
    
    /**
     * 
     * @return 
     */
    
    // <editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public Map<String, Object> getElements() {
        return elements;
    }

    public void setElements(Map<String, Object> elements) {
        this.elements = elements;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }
    // </editor-fold>
}
