package org.brailleblaster.wordprocessor;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Shell;

public class DaisyView
{

private StyledText daisy;

public DaisyView (Shell documentWindow) {
daisy = new StyledText
    (documentWindow, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
}

}

