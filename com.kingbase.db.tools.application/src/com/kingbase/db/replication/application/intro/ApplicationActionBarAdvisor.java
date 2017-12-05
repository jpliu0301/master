package com.kingbase.db.replication.application.intro;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.kingbase.db.replication.application.action.OpenPreferencesAction;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction introAction;
	private IWorkbenchAction perferenceAction;
	private IWorkbenchAction helpContenstsAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer, String viewId) {
		super(configurer);
	}

	protected void makeActions(IWorkbenchWindow window) {
		introAction = ActionFactory.INTRO.create(window);
		register(introAction);

		perferenceAction = PREFERENCES.create(window);
		register(perferenceAction);

		
		helpContenstsAction = ActionFactory.HELP_CONTENTS.create(window);  
        register(helpContenstsAction);  
	}

	@SuppressWarnings("restriction")
	protected void fillMenuBar(IMenuManager menuBar) {

	}

	protected ActionFactory PREFERENCES = new ActionFactory("preferences", //$NON-NLS-1$
			IWorkbenchCommandConstants.WINDOW_PREFERENCES) {

		@Override
		public IWorkbenchAction create(IWorkbenchWindow window) {
			if (window == null) {
				throw new IllegalArgumentException();
			}
			IWorkbenchAction action = new OpenPreferencesAction(window);
			action.setId(getId());

			return action;
		}
	};
}
