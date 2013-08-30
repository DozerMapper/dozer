/**
 * Copyright 2005-2013 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dozer.eclipse.plugin.sourcepage.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.springframework.ide.eclipse.beans.ui.editor.contentassist.BeansJavaCompletionProposal;

public class DozerJavaCompletionProposal extends BeansJavaCompletionProposal {

	public DozerJavaCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, int cursorPosition,
			Image image, String displayString,
			IContextInformation contextInformation, int relevance,
			boolean updateReplacementLengthOnValidate, Object proposedObject) {

		super(replacementString, replacementOffset, replacementLength, cursorPosition-2, image, displayString, contextInformation, relevance, updateReplacementLengthOnValidate, proposedObject);
		replacementString = replacementString.substring(1);
		this.setReplacementString(replacementString);
	}

	@Override
	public void apply(IDocument document) {
		CompletionProposal proposal = new CompletionProposal(
				getReplacementString(), getReplacementOffset(),
				getReplacementLength(), getCursorPosition() + 1,
				getImage(), getDisplayString(), getContextInformation(),
				getAdditionalProposalInfo());
		proposal.apply(document);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension#apply(org.eclipse.jface.text.IDocument,
	 * char, int)
	 */
	@Override
	public void apply(IDocument document, char trigger, int offset) {
		CompletionProposal proposal = new CompletionProposal(
				getReplacementString(), getReplacementOffset(),
				getReplacementLength(), getCursorPosition() + 1, getImage(),
				getDisplayString(), getContextInformation(),
				getAdditionalProposalInfo());
		proposal.apply(document);
	}

	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask,
			int offset) {
		IDocument document = viewer.getDocument();
		// CMVC 252634 to compensate for "invisible" initial region
		int caretOffset = viewer.getTextWidget().getCaretOffset();
		if (viewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) viewer;
			caretOffset = extension.widgetOffset2ModelOffset(caretOffset);
		}
		else {
			caretOffset = viewer.getTextWidget().getCaretOffset()
					+ viewer.getVisibleRegion().getOffset();
		}

		if (caretOffset == getReplacementOffset()) {
			apply(document);
		}
		else {
			// replace the text without affecting the caret Position as this
			// causes the cursor to move on its own
			try {
				int endOffsetOfChanges = getReplacementString().length()
						+ getReplacementOffset();
				// Insert the portion of the new text that comes after the
				// current caret position
				if (endOffsetOfChanges >= caretOffset) {
					int postCaretReplacementLength = getReplacementOffset()
							+ getReplacementLength() - caretOffset;
					int preCaretReplacementLength = getReplacementString()
							.length()
							- (endOffsetOfChanges - caretOffset);
					if (postCaretReplacementLength < 0) {
						if (Debug.displayWarnings) {
							System.out
									.println("** postCaretReplacementLength was negative: " + postCaretReplacementLength); //$NON-NLS-1$
						}
						// This is just a quick fix while I figure out what
						// replacement length is supposed to be
						// in each case, otherwise we'll get negative
						// replacment length sometimes
						postCaretReplacementLength = 0;
					}

					String charAfterCursor = document.get(caretOffset, 1);
					if ("\"".equals(charAfterCursor)) {
						document
								.replace(
										caretOffset,
										((postCaretReplacementLength - 1) < 0 ? postCaretReplacementLength
												: postCaretReplacementLength - 1),
										getReplacementString().substring(
												preCaretReplacementLength));
					}
					else {
						document.replace(caretOffset,
								postCaretReplacementLength,
								getReplacementString().substring(
										preCaretReplacementLength));
					}

				}
				// Insert the portion of the new text that comes before the
				// current caret position
				// Done second since offsets would change for the post text
				// otherwise
				// Outright insertions are handled here
				if (caretOffset > getReplacementOffset()) {
					int preCaretTextLength = caretOffset
							- getReplacementOffset();
					document.replace(getReplacementOffset(),
							preCaretTextLength, getReplacementString()
									.substring(0, preCaretTextLength));
				}
			}
			catch (BadLocationException x) {
				apply(document);
			}
			catch (StringIndexOutOfBoundsException e) {
				apply(document);
			}
		}
	}
	
}
