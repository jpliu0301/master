package com.kingbase.db.core.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class KBProgressMonitorDialog extends ProgressMonitorDialog {

	public KBProgressMonitorDialog(Shell parent) {
		super(parent);
	}
	 @Override
	public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable)
			throws InvocationTargetException, InterruptedException {
		// TODO Auto-generated method stub
		super.run(fork, cancelable, runnable);
	}
	@Override
	protected void createCancelButton(Composite parent) {
		//重写时，不需要添加取消按钮
	}

}
