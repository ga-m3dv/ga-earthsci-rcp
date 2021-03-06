/*******************************************************************************
 * Copyright 2013 Geoscience Australia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package au.gov.ga.earthsci.common.ui.dialogs;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * {@link ErrorDialog} subclass that shows the error's stacktrace in the details
 * view.
 * 
 * @author Michael de Hoog (michael.dehoog@ga.gov.au)
 */
public class StackTraceDialog extends ErrorDialog
{
	private final static String newline = System.getProperty("line.separator"); //$NON-NLS-1$
	private IStatus status;
	private String stackTrace;
	private Clipboard clipboard;

	public StackTraceDialog(Shell parentShell, String dialogTitle, String message, IStatus status, int displayMask)
	{
		super(parentShell, dialogTitle, message, status, displayMask);
		this.status = status;
	}

	public static int openError(Shell parentShell, String title, String message, IStatus status)
	{
		return openError(parentShell, title, message, status, IStatus.OK | IStatus.INFO | IStatus.WARNING
				| IStatus.ERROR);
	}

	public static int openError(Shell parentShell, String title, String message, IStatus status, int displayMask)
	{
		StackTraceDialog dialog = new StackTraceDialog(parentShell, title, message, status, displayMask);
		return dialog.open();
	}

	@Override
	protected List createDropDownList(Composite parent)
	{
		List list = super.createDropDownList(parent);
		list.removeAll();

		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		if (status != null && status.getException() != null)
		{
			status.getException().printStackTrace(printWriter);
		}
		stackTrace = writer.getBuffer().toString();

		String[] lines = stackTrace.split(newline);
		for (String line : lines)
		{
			list.add(line.replace("\t", "    ")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// Replace the default copy behaviour with one that copies the stack trace to the clipboard
		MenuItem copyItem = list.getMenu().getItems()[0];
		copyItem.removeListener(SWT.Selection, copyItem.getListeners(SWT.Selection)[0]);
		copyItem.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				copyToClipboard();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				copyToClipboard();
			}
		});

		return list;
	}

	private void copyToClipboard()
	{
		if (clipboard != null)
		{
			clipboard.dispose();
		}
		clipboard = new Clipboard(getShell().getDisplay());
		clipboard.setContents(new Object[] { stackTrace.toString() },
				new Transfer[] { TextTransfer.getInstance() });
	}

	@Override
	public boolean close()
	{
		if (clipboard != null)
		{
			clipboard.dispose();
		}
		return super.close();
	}
}
