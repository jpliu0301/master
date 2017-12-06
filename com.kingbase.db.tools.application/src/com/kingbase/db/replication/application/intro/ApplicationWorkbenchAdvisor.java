package com.kingbase.db.replication.application.intro;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return Perspective.Perspective_ID;
	}
	
	@Override
	public void postStartup() {
        super.postStartup();

        if(PlatformUI.getWorkbench().getActiveWorkbenchWindow()==null){
        	System.out.println("测试一下");
        	return ;
        }
        IWorkbenchPage PAGE = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorPart[] editorPart=PAGE.getEditors();
        for (int j = 0; j < editorPart.length; j++) {
        	PAGE.closeEditor(editorPart[j], true);
        }
    }
}
