package org.ebookdroid.core;

import org.ebookdroid.core.actions.ActionController;
import org.ebookdroid.core.actions.ActionEx;
import org.ebookdroid.core.actions.ActionMethod;
import org.ebookdroid.core.actions.ActionMethodDef;
import org.ebookdroid.core.actions.ActionTarget;
import org.ebookdroid.core.models.DocumentModel;

import android.app.Dialog;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.kandi.settings.R;

@ActionTarget(
// action
actions = {
        // start
        @ActionMethodDef(id = R.id.actions_gotoPage, method = "goToPageAndDismiss")
// finish
})
public class GoToPageDialog extends Dialog {

    final IViewerActivity base;
    ActionController<GoToPageDialog> actions;

    public GoToPageDialog(final IViewerActivity base) {
        super(base.getContext(),R.style.my_dialog);
        this.base = base;
        this.actions = new ActionController<GoToPageDialog>(base.getActivity(), this);

        setTitle(R.string.dialog_title_goto_page);
        setContentView(R.layout.gotopage);

        final Button button = (Button) findViewById(R.id.goToButton);
        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekbar);
        final EditText editText = (EditText) findViewById(R.id.pageNumberTextEdit);

        button.setOnClickListener(actions.getOrCreateAction(R.id.actions_gotoPage));
        editText.setOnEditorActionListener(actions.getOrCreateAction(R.id.actions_gotoPage));

        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
                if (fromUser) {
                    updateControls(progress, false);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        final DocumentModel dm = base.getDocumentModel();
        final Page lastPage = dm != null ? dm.getLastPageObject() : null;
        final int current = dm != null ? dm.getCurrentViewPageIndex() : 0;
        final int max = lastPage != null ? lastPage.index.viewIndex : 0;

        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekbar);
        seekbar.setMax(max);

        updateControls(current, true);
    }

    @ActionMethod(ids = R.id.actions_gotoPage)
    public void goToPageAndDismiss(final ActionEx action) {
        navigateToPage();
        dismiss();
    }

    private void updateControls(final int viewIndex, final boolean updateBar) {
        final SeekBar seekbar = (SeekBar) findViewById(R.id.seekbar);
        final EditText editText = (EditText) findViewById(R.id.pageNumberTextEdit);

        editText.setText("" + (viewIndex + 1));
        editText.selectAll();

        if (updateBar) {
            seekbar.setProgress(viewIndex);
        }
    }

    private void navigateToPage() {
        final EditText text = (EditText) findViewById(R.id.pageNumberTextEdit);
        int pageNumber = 1;
        try {
            pageNumber = Integer.parseInt(text.getText().toString());
        } catch (final Exception e) {
            pageNumber = 1;
        }
        final int pageCount = base.getDocumentModel().getPageCount();
        if (pageNumber < 1 || pageNumber > pageCount) {
            Toast.makeText(getContext(), base.getContext().getString(R.string.bookmark_invalid_page) + pageCount, 2000)
                    .show();
            return;
        }
        base.getDocumentController().goToPage(pageNumber - 1);
    }
}
