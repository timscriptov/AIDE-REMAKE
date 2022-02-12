package com.trunksku.asmobile.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineBackgroundSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.textclassifier.TextClassifier;
import android.widget.EditText;
import android.widget.TextView;

import com.trunksku.asmobile.R;
import com.trunksku.asmobile.editor.UndoRedoUtils;
import com.trunksku.asmobile.editor.highlight.ColorDefault;
import com.trunksku.asmobile.editor.patterns.Patterns;
import com.trunksku.asmobile.editor.patterns.PatternsGradle;
import com.trunksku.asmobile.editor.patterns.PatternsJava;
import com.trunksku.asmobile.editor.patterns.PatternsXml;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * Editor class to handle code highlighting etc
 * Derived from: https://github.com/markusfisch/ShaderEditor/blob/master/app/src/main/java/de/markusfisch/android/shadereditor/widget/ShaderEditor.java
 */
public class CodeEditor extends EditText {
    public static final String ERROR_COLOR = "#ff0000";
    public static final String WARNING_COLOR = "#E69317";
    public static final char CURSOR = '\u2622';
    public static final float DISTANCE_TO_ZOOM = 10;
    private static final int HIGHLIGHTER_COLOR = 0xffefefef;
    static UndoRedoUtils undoRedo;
    public final int GRID_GAP = 20;
    private final String TAG = CodeEditor.class.getSimpleName();
    /**
     * Handler used to update colours when code is changed
     */
    private final Handler updateHandler = new Handler();
    /**
     * Context used to get preferences
     */
    private final Context context;
    private final Paint mCursorLinePaint = new Paint();
    /**
     * Custom listener
     */
    public OnTextChangedListener onTextChangedListener = null;
    /**
     * Delay used to update code
     */
    public int updateDelay = 2000;
    int currentLine = 0;
    int lineDiff = 0;
    /**
     * Rect to represent each line
     */
    Paint highlightPaint = new Paint();
    boolean mBracketMatching = true;
    int first_pointer_id = 0;
    int second_pointer_id = 1;
    double startDistance = -1;
    boolean canTouch = true;
    int mCharHeight = 0;
    int mGutterWidth = 10;
    int h;
    /**
     * Type of code set
     */
    private CodeType codeType;
    /**
     * Checks if code has been changed
     */
    private boolean fileModified = true;
    private Rect lineRect;
    /**
     * Paint to draw line numbers
     */
    private Paint lineShadowPaint;
    private boolean isHighlighting;
    private ColorDefault colors;
    private Patterns patterns;
    private PatternsGradle patternsGradle;
    //private PatternsJson patternsJson;
    private PatternsXml patternsXml;
    private PatternsJava patternsJava;
    /**
     * Runnable used to update colours when code is changed
     */
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isHighlighting) {
                Editable e = getText();
                if (onTextChangedListener != null)
                    onTextChangedListener.onTextChanged(e.toString());
                highlightWithoutChange(e);
            }
        }
    };
    private boolean hasLineNumbers;
    private BackgroundColorSpan mOpenBracketSpan;
    private BackgroundColorSpan mClosedBracketSpan;
    private int lineNumber;
    private Rect lineBounds;
    private Paint paint;
    private Rect rect;
    private float gridWidth = 1;
    private int numHLines;
    private int numVLines;
    private Paint gridLinePaint;
    private ScaleGestureDetector mScaleGestureDetector;
    /*private GestureDetector gestureDetector;

    private ScaleGestureDetector mScaleDetector;
    private float mScale = 1f;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        mScaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // double tap fired.
            return true;
        }
    }*/
    private boolean mScaling = false;

    /**
     * Highlights given code and sets it as text
     *
     * @param text to be highlighted
     */


    /**
     * Public constructor
     *
     * @param context used to get preferences
     */
    public CodeEditor(Context context) {
        this(context, null);
    }

    /**
     * Public constructor
     *
     * @param context used to get preferences
     * @param attrs   not used
     */
    public CodeEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public static double getDistanceBetween(float x1, float y1, float x2, float y2) {
        float distance_x = Math.abs(x1 - x2);
        float distance_y = Math.abs(y1 - y2);
        return Math.sqrt(distance_x * distance_x + distance_y * distance_y);
    }

    public static void clearSpans(Spannable spannable, Class<?> spanClass, int start_pos, int end_pos) {
        for (Object span : spannable.getSpans(start_pos, end_pos, spanClass)) {
            spannable.removeSpan(span);
        }
    }

    public static int dpToPx(@NonNull Context context, int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /**
     * Code used to initialise editor
     */
    private void init() {
        setEditableFactory(new Editable.Factory() {
            @Override
            public Editable newEditable(CharSequence source) {
                return new SpannableStringBuilder(source) {

                    @Override
                    public IntStream chars() {
                        return null;
                    }

                    @Override
                    public IntStream codePoints() {
                        return null;
                    }

                    @Override
                    public void setSpan(Object what, int start, int end,
                                        int flags) {

                        super.setSpan(what, start, end, flags);
                    }
                };
                //return new QuicklySpannableStringBuilder(source);
                //return super.newEditable(source);
            }
        });
        setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        setInputType(InputType.TYPE_NULL | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        // setPrivateImeOptions("nm");
        //setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
        }
        setTypeface(Typeface.MONOSPACE);
        setIncludeFontPadding(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setTextClassifier(TextClassifier.NO_OP);
        }
        colors = new ColorDefault();

        lineBounds = new Rect();
        highlightPaint = new Paint();
        highlightPaint.setColor(Color.GRAY);


        rect = new Rect();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(colors.getColorNumber());

        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        paint.setTextSize(getTextSize() - scaledDensity * 1.2f);
        paint.setTypeface(Typeface.MONOSPACE);


        patterns = new Patterns();
        patternsGradle = new PatternsGradle();
        //patternsJson = new PatternsJson();
        patternsXml = new PatternsXml();
        patternsJava = new PatternsJava();

        lineRect = new Rect();
        hasLineNumbers = true;

        mOpenBracketSpan = new BackgroundColorSpan(colors.getColorParams());
        mClosedBracketSpan = new BackgroundColorSpan(colors.getColorParams());


        lineShadowPaint = new Paint();
        lineShadowPaint.setStyle(Paint.Style.FILL);
        lineShadowPaint.setColor(colors.getColorLineShadow());

        mCursorLinePaint.setColor(colors.getColorLineShadow());

        //setGridLineConfig();

        //setTextScale(Setting.mConfig.mEditorConfig.mFontScale);
        setLineSpacing(0, 1.12f * 1.0f);

        setHighlightColor(colors.getColorLineHighlight());

        setBackgroundColor(Color.TRANSPARENT);

        setGravity(Gravity.START);
        setTextColor(colors.getColorText());

        invalidateCharHeight();
        setupUndoRedo();
        setDefaultFilters();
        setOnCreateContextMenuListener(new CMenu());
        //setActionMode();

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setupAutoComplete();
            }
        });
		/*gestureDetector = new GestureDetector(getContext(), new GestureListener());

        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener()
			{
				@Override
				public boolean onScale(ScaleGestureDetector detector)
				{
					float scale = 1 - detector.getScaleFactor();


					float prevScale = mScale;
					mScale += scale;

					if (mScale < 0.1f) // Minimum scale condition:
						mScale = 0.1f;

					if (mScale > 10f) // Maximum scale condition:
						mScale = 10f;
					ScaleAnimation scaleAnimation = new ScaleAnimation(1f / prevScale, 1f / mScale, 1f / prevScale, 1f / mScale, detector.getFocusX(), detector.getFocusY());
					scaleAnimation.setDuration(0);
					scaleAnimation.setFillAfter(true);
					startAnimation(scaleAnimation);
					//hscroll.startAnimation(scaleAnimation);
					//getRootView().findViewById(R.id.svSeats).startAnimation(scaleAnimation);
					return true;
				}
			});*/
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                Log.i(TAG, "onScaleEnd");
                getParent().requestDisallowInterceptTouchEvent(false);
                mScaling = false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                Log.i(TAG, "onScaleBegin");
                mScaling = true;
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                Log.i(TAG, "onScale");
                //float scale = mTextScale*detector.getScaleFactor();
                //setTextScale(scale);
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent:" + event.getAction());
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN && event.getPointerCount() == 2) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP && event.getPointerCount() <= 1) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        mScaleGestureDetector.onTouchEvent(event);
        if (mScaling)
            return true;
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        //drawCursorLine(canvas);
        //drawGridLines(canvas);
        if (true) {
            lineNumber = getLayout().getLineForOffset(getSelectionStart());
            getLineBounds(lineNumber, lineBounds);

            canvas.drawRect(lineBounds, highlightPaint);
        }

        if (true) {

            int baseline;
            int lineCount = getLineCount();
            int lineNumber = 1;

            for (int i = 0; i < lineCount; ++i) {
                baseline = getLineBounds(i, null);
                if (i == 0) {
                    canvas.drawText("" + lineNumber, rect.left + 10, baseline, paint);
                    ++lineNumber;
                } else if (getText().charAt(getLayout().getLineStart(i) - 1) == '\n') {
                    canvas.drawText("" + lineNumber, rect.left + 10, baseline, paint);
                    ++lineNumber;
                }
            }

            if (lineCount < 100) {
                setPadding(80, getPaddingTop(), getPaddingRight(), getPaddingBottom());
            } else if (lineCount < 1000) {
                setPadding(90, getPaddingTop(), getPaddingRight(), getPaddingBottom());
            } else if (lineCount < 10000) {
                setPadding(100, getPaddingTop(), getPaddingRight(), getPaddingBottom());
            } else if (lineCount < 100000) {
                setPadding(110, getPaddingTop(), getPaddingRight(), getPaddingBottom());
            }
        }
        // drawing vertical divider
        if (true) {
            int x = getScrollX() + getCompoundPaddingLeft() - 8;
            int y = Math.max(getLineHeight() * getLineCount(), getHeight()) + getExtendedPaddingBottom();
            mGutterWidth = x;
            canvas.drawLine(x, 0, x, y, lineShadowPaint);
        }


        super.onDraw(canvas);
    }

    private void drawGridLines(Canvas canvas) {
        for (int i = 0; i <= numHLines; i++)
            canvas.drawLine(0, GRID_GAP * i, super.getWidth(), GRID_GAP * i, gridLinePaint);
        for (int i = 0; i < numVLines; i++)
            canvas.drawLine(GRID_GAP * i, 0, GRID_GAP * i, super.getHeight(), gridLinePaint);
    }

    private void setGridLineConfig() {
        numHLines = super.getHeight() / GRID_GAP;
        numVLines = super.getWidth() / GRID_GAP;
        gridLinePaint = new Paint();
        gridLinePaint.setColor(Color.GRAY);
        gridLinePaint.setStyle(Paint.Style.STROKE);
        gridLinePaint.setStrokeWidth(gridWidth);
    }

    protected void invalidateCharHeight() {
        mCharHeight = (int) Math.ceil(getPaint().getFontSpacing());
        mCharHeight = (int) getPaint().measureText("M");
    }

    private void setDefaultFilters() {
        //indent filters
        final InputFilter indentFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, final int start, final int end,
                                       final Spanned dest, final int dstart, final int dend) {
                // if (mIsAutoIndent) {
                if (!(source.length() == 1 && source.charAt(0) == '\n')) {
                    return null;
                }
                int startIndex = dstart - 1;
                if (startIndex < 0 || startIndex >= dest.length())
                    return null;

                char ch;
                for (; startIndex >= 0; startIndex--) {
                    ch = dest.charAt(startIndex);
                    if (ch != '\r')
                        break;
                }

                StringBuilder indent = new StringBuilder();
                for (int i = startIndex; i >= 0; i--) {
                    ch = dest.charAt(i);
                    if (ch == '\n' || ch == '\r') {
                        break;
                    } else if (ch == ' ' || ch == '\t') {
                        indent.append(ch);
                    } else {
                        indent.setLength(0);
                    }
                }
                indent.reverse();

                if (dend < dest.length() && dest.charAt(dend) == '}'
                        && dstart - 1 >= 0 && dest.charAt(dstart - 1) == '{') {
                    int mstart = dstart - 2;
                    while (mstart >= 0 && dest.charAt(mstart) != '\n') {
                        mstart--;
                    }
                    String closeIndent = "";
                    if (mstart >= 0) {
                        mstart++;
                        int zstart = mstart;
                        while (zstart < dest.length() && dest.charAt(zstart) == ' ') {
                            zstart++;
                        }
                        closeIndent = dest.toString().substring(mstart, zstart);
                    }
                    return source +
                            (indent.toString() + "  ") +
                            CURSOR + "\n" + closeIndent;
                }

                return "\n" + indent.toString();
                // }
                // return null;
            }
        };

        //end line filter, only support \n
        InputFilter newLineFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                       int dstart, int dend) {
                final String s = source.toString();
                if (s.contains("\r")) {
                    return s.replace("\r", "");
                }
                return null;
            }
        };

        //bracket filter, auto add close bracket if auto pair is enable
        final InputFilter bracketFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {
                // if (mIsAutoPair) {
                if (end - start == 1 && start < source.length() && dstart < dest.length()) {
                    char c = source.charAt(start);
                    if (c == '(' || c == '{' || c == '[' || c == '"' || c == '\'') {
                        return addBracket(source, start);
                    }
                }
                // }
                return null;
            }
        };

        setFilters(new InputFilter[]{indentFilter, newLineFilter, bracketFilter});

        //auto add bracket
        addTextChangedListener(new TextWatcher() {
            private int start;
            private int count;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.start = start;
                this.count = count;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > start && count > 1) {
                    for (int i = start; i < start + count; i++) {
                        if (editable.charAt(i) == CURSOR) {
                            editable.delete(i, i + 1);
                            setSelection(start);
                            break;
                        }
                    }
                }
            }
        });
    }

    private CharSequence addBracket(CharSequence source, int start) {
        switch (source.charAt(start)) {
            case '"':
                return "\"" + CURSOR + "\"";
            case '\'':
                return "'" + CURSOR + "'";
            case '(':
                return "(" + CURSOR + ")";
            case '{':
                return "{" + CURSOR + "}";
            case '[':
                return "[" + CURSOR + "]";
        }
        return null;
    }

    public void setFilters() {
        setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (end - start == 1 && start < source.length() && dstart < dest.length()) {
                    char c = source.charAt(start);
                    if (c == '\n') return autoIndent(source, dest, dstart, dend);
                }

                return source;
            }
        }});
    }

    public CharSequence autoIndent(CharSequence source, Spanned dest, int d_start, int d_end) {
        String indent = "";
        int sequenceStart = d_start - 1;
        int sequenceEnd;

        // find start of this line
        boolean stringFound = false;
        int pt = 0;

        for (; sequenceStart > -1; --sequenceStart) {
            char c = dest.charAt(sequenceStart);

            if (c == '\n')
                break;

            if (c != ' ' && c != '\t') {
                if (!stringFound) {
                    // indent always after those characters
                    if (c == '{' || c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^' || c == '=')
                        --pt;

                    stringFound = true;
                }

                // parenthesis counter
                if (c == '(')
                    --pt;
                else if (c == ')')
                    ++pt;
            }
        }

        // copy indent of this line into the next
        char charAtCursor = ' ';
        if (sequenceStart > -1) {
            charAtCursor = dest.charAt(d_start);

            for (sequenceEnd = ++sequenceStart; sequenceEnd < d_end; ++sequenceEnd) {
                char lastChar = dest.charAt(sequenceEnd);

                // auto expand comments
                if (charAtCursor != '\n' && lastChar == '/' && sequenceEnd + 1 < d_end && dest.charAt(sequenceEnd) == lastChar) {
                    sequenceEnd += 2;
                    break;
                }

                if (lastChar != ' ' && lastChar != '\t')
                    break;
            }

            indent += dest.subSequence(sequenceStart, sequenceEnd);
        }

        // add new indent
        if (pt < 0) {
            String old_indent = indent;
            indent += "    "; // (4 spaces)
            if (charAtCursor == '}' || charAtCursor == ')' || charAtCursor == ']')
                indent += "\n" + old_indent;
        }

        // append white space of previous line and new indent
        return source + indent;
    }

    public void setupUndoRedo() {
        undoRedo = new UndoRedoUtils(this);
    }

    public void undo() {
        undoRedo.undo();
    }

    public void redo() {
        undoRedo.redo();
    }

    public void clearHistory() {
        undoRedo.clearHistory();
    }

    /**
     * Prevent code from updating
     */
    private void cancelUpdate() {
        updateHandler.removeCallbacks(updateRunnable);
    }

    public void setTextHighlighted(CharSequence text) {
        cancelUpdate();
        fileModified = false;

        try {
            setText(highlight(new SpannableStringBuilder(text)));
        } catch (Exception e) {

        }
        fileModified = true;
        if (onTextChangedListener != null) onTextChangedListener.onTextChanged(text.toString());
    }

    /**
     * Used in main runnable
     *
     * @param e text to be highlighted
     */
    private void highlightWithoutChange(Editable e) {
        fileModified = false;
        highlight(e);
        fileModified = true;
    }

    /**
     * Main method used for highlighting i.e. this is where the magic happens
     *
     * @param e text to be highlighted
     * @return highlighted text
     */
    private Editable highlight(Editable e) {
        isHighlighting = true;

        try {
            if (e.length() == 0) return e;
            if (hasSpans(e)) clearSpans(e);

            Matcher m;
            switch (codeType) {
                case HTML:
                    for (m = patterns.getPatternKeywords().matcher(e); m.find(); ) {
                        if (e.toString().charAt(m.start() - 1) == '<' || e.toString().charAt(m.start() - 1) == '/') {
                            e.setSpan(new ForegroundColorSpan(colors.getColorKeyword()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                    for (m = patterns.getPatternBuiltins().matcher(e); m.find(); ) {
                        if (e.toString().charAt(m.start() - 1) == ' ' && e.toString().charAt(m.end()) == '=') {
                            e.setSpan(new ForegroundColorSpan(colors.getColorBuiltin()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                    for (m = patterns.getPatternStrings().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorStrings()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patterns.getPatternComments().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorComment()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                case CSS:
                    for (m = patterns.getPatternKeywords().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorKeyword()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patterns.getPatternParams().matcher(e); m.find(); ) {
                        if (e.toString().charAt(m.end()) == ':') {
                            e.setSpan(new ForegroundColorSpan(colors.getColorParams()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                    for (int index = e.toString().indexOf(":"); index >= 0; index = e.toString().indexOf(":", index + 1)) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorEnding()), index + 1, e.toString().indexOf(";", index + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (int index = e.toString().indexOf("."); index >= 0; index = e.toString().indexOf(".", index + 1)) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorBuiltin()), index + 1, e.toString().indexOf("{", index + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (int index = e.toString().indexOf("#"); index >= 0; index = e.toString().indexOf("#", index + 1)) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorBuiltin()), index + 1, e.toString().indexOf("{", index + 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patterns.getPatternEndings().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorEnding()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patterns.getPatternStrings().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorStrings()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patterns.getPatternCommentsOther().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorComment()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                case JS:
                    for (m = patterns.getPatternDatatypes().matcher(e); m.find(); ) {
                        if (e.toString().charAt(m.end()) == ' ') {
                            e.setSpan(new ForegroundColorSpan(colors.getColorParams()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                    for (m = patterns.getPatternFunctions().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorFunctions()), m.start() + 2, m.end() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patterns.getPatternSymbols().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorKeyword()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (int index = e.toString().indexOf("null"); index >= 0; index = e.toString().indexOf("null", index + 1)) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorEnding()), index, index + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patterns.getPatternNumbers().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorBuiltin()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patterns.getPatternBooleans().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorBuiltin()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patterns.getPatternStrings().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorStrings()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patterns.getPatternCommentsOther().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorComment()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                case JAVA:
                    for (m = patternsJava.getPatternKeywords().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorKeyword()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsJava.getPatternImports().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorFunctions()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsJava.getPatternAnnotations().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(PatternsJava.colorAnnotations), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    /*for (int index = e.toString().indexOf("null"); index >= 0; index = e.toString().indexOf("null", index + 1)) {
                     e.setSpan(new ForegroundColorSpan(colors.getColorEnding()), index, index + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                     }*/

                    for (m = patternsJava.getPatternNumbers().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorBuiltin()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsJava.getPatternBooleans().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorBuiltin()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsJava.getPatternStrings().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorStrings()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsJava.getPatternComments().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorComment()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsJava.getPatternCommentsOther().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorComment()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                case GRADLE:
                    for (m = patternsGradle.getPatternFunctions().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorFunctions()), m.start() + 2, m.end() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsGradle.getPatternSymbols().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorKeyword()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (int index = e.toString().indexOf("null"); index >= 0; index = e.toString().indexOf("null", index + 1)) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorEnding()), index, index + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsGradle.getPatternNumbers().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorBuiltin()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsGradle.getPatternBooleans().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorBuiltin()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsGradle.getPatternStrings().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorStrings()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsGradle.getPatternCommentsOther().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorComment()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
                    /* case JSON:
                     for (m = patternsJson.getPatternFunctions().matcher(e); m.find(); ) {
                     e.setSpan(new ForegroundColorSpan(colors.getColorFunctions()), m.start() + 2, m.end() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                     }

                     for (int index = e.toString().indexOf("null"); index >= 0; index = e.toString().indexOf("null", index + 1)) {
                     e.setSpan(new ForegroundColorSpan(colors.getColorEnding()), index, index + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                     }

                     for (m = patternsJson.getPatternNumbers().matcher(e); m.find(); ) {
                     e.setSpan(new ForegroundColorSpan(colors.getColorBuiltin()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                     }

                     for (m = patternsJson.getPatternBooleans().matcher(e); m.find(); ) {
                     e.setSpan(new ForegroundColorSpan(colors.getColorBuiltin()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                     }

                     for (m = patternsJson.getPatternStrings().matcher(e); m.find(); ) {
                     e.setSpan(new ForegroundColorSpan(colors.getColorStrings()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                     }

                     for (m = patternsJson.getPatternCommentsOther().matcher(e); m.find(); ) {
                     e.setSpan(new ForegroundColorSpan(colors.getColorComment()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                     }
                     break;*/
                case XML:
                    for (m = patternsXml.getPatternStartElement().matcher(e); m.find(); ) {
                        if (e.toString().charAt(m.start() - 1) == '<' || e.toString().charAt(m.start() - 1) == '/') {
                            e.setSpan(new ForegroundColorSpan(PatternsXml.colorStartElement), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                    for (m = patternsXml.getPatternEndElement().matcher(e); m.find(); ) {
                        if (e.toString().charAt(m.start() - 1) == '<' || e.toString().charAt(m.start() - 1) == '/') {
                            e.setSpan(new ForegroundColorSpan(PatternsXml.colorEndElement), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                    for (m = patternsXml.getPatternStrings().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorStrings()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsXml.getPatternStrings().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(PatternsXml.colorStrings), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsXml.getPatternStartTag().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(PatternsXml.colorStartTag), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsXml.getPatternEndTag().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(PatternsXml.colorEndTag), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsXml.getPatternComments().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(colors.getColorComment()), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsXml.getPatternAfterXmlns().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(PatternsXml.colorStartTag), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    for (m = patternsXml.getPatternXmlns().matcher(e); m.find(); ) {
                        e.setSpan(new ForegroundColorSpan(PatternsXml.colorEndTag), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    break;
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }

        isHighlighting = false;
        return e;
    }

    /**
     * Method to set the code type
     *
     * @param type of code
     */
    public void setType(CodeType type) {
        codeType = type;
    }

    /**
     * Removes all spans
     *
     * @param e text to be cleared
     */

    public void setReadOnly(boolean readOnly) {
        setFocusable(!readOnly);
        setFocusableInTouchMode(!readOnly);
    }

    public void setCursorColor(@ColorInt int color) {
        try {
            // Get the cursor resource id
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(this);

            // Get the editor
            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(this);

            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(getContext(), drawableResId);
            if (drawable != null) {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
            Drawable[] drawables = {drawable, drawable};

            // Set the drawables
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception e) {
            //Logger.error(TAG, e);
        }
    }

    private void clearSpans(Editable e) {
        {
            ForegroundColorSpan spans[] = e.getSpans(0, e.length(), ForegroundColorSpan.class);
            for (int n = spans.length; n-- > 0; ) e.removeSpan(spans[n]);
        }
    }

	/*@Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        numberPaint.setTextSize(getTextSize());
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        numberPaint.setTextSize(getTextSize());
    }*/

    /*@Override
     public void setText(CharSequence text, BufferType type) {
     // setTextCalled = true;
     super.setText(text, type);
     }*/


	/*@Override
     public boolean onTouchEvent(MotionEvent event) {
     if (hasFocus() && getSelectionStart() == getSelectionEnd())
     clearFocus();
     performClick();
     return super.onTouchEvent(event);
     }*/

    private boolean hasSpans(Editable e) {
        return e.getSpans(0, e.length(), ForegroundColorSpan.class).length > 0;
    }

    /**
     * Method used to draw line numbers onto code
     *
     * @param canvas used for drawing
     */
	/*@Override
     public int getCompoundPaddingRight() {
     int symbol_offset = (int)(getTextSize() / 1.5);
     return Integer.toString(getLineCount()).length() * symbol_offset + symbol_offset;
     }*/
    @Override
    public int getCompoundPaddingLeft() {
        int symbol_offset = (int) (getTextSize());
        return Integer.toString(getLineCount()).length() * symbol_offset + symbol_offset;
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        if (selStart == selEnd)
            checkMatchingBracket(selStart);
        invalidate();
    }

    private void highlightLine(int line, int color, Canvas canvas) {
        Rect lineBounds = new Rect();
        highlightPaint.setColor(color);
        getLineBounds(line, lineBounds);
        //lineBounds.left = 0;
        canvas.drawRect(lineBounds, lineShadowPaint);
    }

    protected void checkMatchingBracket(int pos) {
        getText().removeSpan(mOpenBracketSpan);
        getText().removeSpan(mClosedBracketSpan);
        char[] LANGUAGE_BRACKETS = new char[]{'{', '[', '(', '}', ']', ')'}; //do not change

        if (mBracketMatching) {
            if (pos > 0 && pos <= getText().length()) {
                char c1 = getText().charAt(pos - 1);
                for (int i = 0; i < LANGUAGE_BRACKETS.length; i++) {
                    if (LANGUAGE_BRACKETS[i] == c1) {
                        char c2 = LANGUAGE_BRACKETS[(i + 3) % 6];
                        boolean open = false;
                        if (i <= 2) {
                            open = true;
                        }
                        int k;
                        if (open) {
                            int nob = 1;
                            for (k = pos; k < getText().length(); k++) {
                                if (getText().charAt(k) == c2) {
                                    nob--;
                                }
                                if (getText().charAt(k) == c1) {
                                    nob++;
                                }
                                if (nob == 0) {
                                    showBracket(pos - 1, k);
                                    break;
                                }
                            }
                        } else {
                            int ncb = 1;
                            for (k = pos - 2; k >= 0; k--) {
                                if (getText().charAt(k) == c2) {
                                    ncb--;
                                }
                                if (getText().charAt(k) == c1) {
                                    ncb++;
                                }
                                if (ncb == 0) {
                                    showBracket(k, pos - 1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Подсветка найденных скобок.
     *
     * @param i - позиция первой скобки.
     * @param j - позиция второй скобки.
     */
    protected void showBracket(int i, int j) {
        getText().setSpan(mOpenBracketSpan, i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        getText().setSpan(mClosedBracketSpan, j, j + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public List<CharSequence> getLines() {
        final List<CharSequence> lines = new ArrayList<>();
        final Layout layout = getLayout();

        if (layout != null) {
            final int lineCount = layout.getLineCount();
            final CharSequence text = layout.getText();

            for (int i = 0, startIndex = 0; i < lineCount; i++) {
                final int endIndex = layout.getLineEnd(i);
                lines.add(text.subSequence(startIndex, endIndex));
                startIndex = endIndex;
            }
        }
        return lines;
    }

    public int getCurrentLine() {
        Layout l = getLayout();
        return l == null ? -1 : l.getLineForOffset(getSelectionStart());
    }

    public int getCurrentCursorLine() {
        int selectionStart = Selection.getSelectionStart(getText());
        Layout layout = getLayout();

        if (!(selectionStart == -1)) {
            return layout.getLineForOffset(selectionStart);
        }

        return -1;
    }

    public void selectLine() {
        int min = Math.min(getSelectionStart(), getSelectionEnd());
        int max = Math.max(getSelectionStart(), getSelectionEnd());
        if (max > min) {
            max--;
        }
        while (max < getText().length() && getText().charAt(max) != '\n') {
            max++;
        }
        while (min > 0 && getText().charAt(min - 1) != '\n') {
            min--;
        }
        setSelection(min, max);
    }

    public void duplicateLine() {
        int min = Math.min(getSelectionStart(), getSelectionEnd());
        int max = Math.max(getSelectionStart(), getSelectionEnd());
        if (max > min) {
            max--;
        }
        while (max < getText().length() && getText().charAt(max) != '\n') {
            max++;
        }
        while (min > 0 && getText().charAt(min - 1) != '\n') {
            min--;
        }
        Editable editableText = getEditableText();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append(getText().subSequence(min, max).toString());
        editableText.insert(max, stringBuilder.toString());
    }

    public void deleteLine() {
        int min = Math.min(getSelectionStart(), getSelectionEnd());
        int max = Math.max(getSelectionStart(), getSelectionEnd());
        if (max > min) {
            max--;
        }
        while (max < getText().length() && getText().charAt(max) != '\n') {
            max++;
        }
        while (min > 0 && getText().charAt(min - 1) != '\n') {
            min--;
        }
        getEditableText().delete(min, max);
    }

    public void insertTab() {
        int start = getSelectionStart();
        int end = getSelectionEnd();

        getText().replace(
                Math.min(start, end),
                Math.max(start, end),
                "\t",
                0,
                1);
    }

    public void gotoLine(int toLine) {
        int realLine = toLine - 1;
        if (realLine == -1) {
            // codeEditor.showToast(mContext.getString(R.string.gotoLine_above_than_0), true);
        } else if (realLine < getLineCount()) {
            setSelection(getLineEndIndex(realLine - 1));
            scrollToLine(realLine - 1);
        } else {
            //codeEditor.showToast(mContext.getString(R.string.gotoLine_not_exists), true);
        }
    }

    public void scrollToLine(int virtualLine) {
        virtualLine = Math.max(0, Math.min(virtualLine, getLineCount() - 1));
        final Layout layout = getLayout();
        final int layoutHeight = layout.getHeight();


        Rect bound = new Rect();
        getGlobalVisibleRect(bound);
        final int visibleHeight = bound.height();

        int y = layout.getLineTop(virtualLine);
        if (layoutHeight - visibleHeight >= 0) {
            y = Math.min(y, layoutHeight - visibleHeight);
        }
        scrollTo(getScrollX(), y);
    }

    public void find(String what, boolean matchCase, boolean regex, boolean wordOnly) {
        Pattern pattern;
        if (regex) {
            if (matchCase) {
                pattern = Pattern.compile(what);
            } else {
                pattern = Pattern.compile(what,
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            }
        } else {
            if (wordOnly) {
                if (matchCase) {
                    pattern = Pattern.compile("\\s" + what + "\\s");
                } else {
                    pattern = Pattern.compile("\\s" + Pattern.quote(what) + "\\s",
                            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                }
            } else {
                if (matchCase) {
                    pattern = Pattern.compile(Pattern.quote(what));
                } else {
                    pattern = Pattern.compile(Pattern.quote(what),
                            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                }
            }
        }
        //Очищаем background-спаны, потому что будем накладывать новые
        clearSpans(true, false);
        for (Matcher m = pattern.matcher(getText()); m.find(); ) {
            getText().setSpan(new BackgroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorSearchSpan)),
                    m.start(),
                    m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    protected int getHeightVisible() {
        Rect rect = new Rect();
        getWindowVisibleDisplayFrame(rect);
        return rect.bottom - rect.top;
    }

    public void setupAutoComplete() {
        //ThreadUtils.getCachedPool().execute(() -> Language.addRtIdentifier(context.getFilesDir() + "/sdk/android.jar"));
        /*ArrayList<String> JAVA_KEYWORDS = new ArrayList<>(Arrays.asList(
                                                                              "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
                                                                              "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
                                                                              "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
                                                                              "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
                                                                              "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"));
        String[] items = patterns.getPatternKeywords().pattern().replace("(", "").replace(")", "").substring(2, patterns.getPatternKeywords().pattern().length() - 2).split("\\|");

        ArrayList<SuggestionItem> data = new ArrayList<>();



        for (String name : JAVA_KEYWORDS) {
            SuggestionItem si = new SuggestionItem();
            si.setType(SuggestionType.TYPE_KEYWORD);
            si.setName(name);
            si.setDesc("");
            data.add(si);
            //data.add(new SuggestionItem(SuggestionType.TYPE_KEYWORD, name)); //Keyword
        }
        mAdapter = new SuggestionAdapter(context, R.layout.item_list_suggest, data);
        setAdapter(mAdapter);


        SymbolsTokenizer mTokenizer = new SymbolsTokenizer();
        setTokenizer(mTokenizer);
        setThreshold(2);
        */

        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // onPopupChangePosition();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                  /* Layout layout = getLayout();
                    int position = getSelectionStart();
                    int line = layout.getLineForOffset(position);
                    int baseline = layout.getLineBaseline(line);
                    int bottom = getHeight();
                    int x = (int) layout.getPrimaryHorizontal(position);

                    if (x + (getWidth() / 2) > getWidth()) {
                        x = getWidth() / 2;
                    }

                     setDropDownVerticalOffset(baseline - bottom);
                     setDropDownHorizontalOffset(x);

                     setDropDownHeight(getHeight() / 3);
                     setDropDownWidth(getWidth() / 2);*/

            }

            @Override
            public void afterTextChanged(Editable e) {
                cancelUpdate();

                if (!fileModified) return;

                updateHandler.postDelayed(updateRunnable, updateDelay);
            }
        });
    }

    public int getLineStartIndex(int line) {
        Pattern pattern = Pattern.compile(".*?\n");
        Matcher matcher = pattern.matcher(getText());
        int i = 0;
        while (matcher.find()) {
            if (line == i)
                return matcher.start();
            i++;
        }
        return -1;
    }

    public int getLineEndIndex(int line) {
        Pattern pattern = Pattern.compile(".*?\n");
        Matcher matcher = pattern.matcher(getText());
        int i = 0;
        while (matcher.find()) {
            if (line == i)
                return matcher.end();
            i++;
        }
        return -1;
    }

    public void startFindMode() {
        startActionMode(new ActionMode.Callback() {
            private int idx;

            private EditText edit;

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    /*mode.setTitle("Search");
                    mode.setSubtitle(null);*/

                edit = new EditText(getContext()) {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                            /*if (s.length() > 0) {
                                idx = 0;
                                findNext();
                            }else selectText(false);*/
                    }
                };
                edit.setSingleLine(true);
                edit.setImeOptions(3);
                  /*  edit.setOnEditorActionListener((p1, p2, p3) -> {
                        findNext();
                        return true;
                    });*/
                edit.setHint("Search");
                edit.setLayoutParams(new LayoutParams(dpToPx(context, 100), -1));
                mode.setCustomView(edit);
                // menu.add(0, 1, 0, "").setActionView(edit);
                menu.add(0, 2, 0, "Find");
                //edit.requestFocus();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {
                    case 1:
                        break;
                    case 2:
                        findLine();
                        //  findNext();
                        break;

                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode p1) {

            }

            public void findLine() {
                String s = edit.getText().toString();
                if (s.isEmpty())
                    return;

                    /*int i = Integer.parseInt(s);
                    gotoLine(i);*/
                find(s, true, true, true);
            }
        });

    }

    public void setError(int start, int end) {
        getText().setSpan(new WaveSpan(Color.parseColor(ERROR_COLOR)), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }

    public void setWarning(int start, int end) {
        getText().setSpan(new WaveSpan(Color.parseColor(WARNING_COLOR)), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }

    public void clearErrors() {
        clearSpans(getText(), WaveSpan.class, 0, getText().length());
    }

    protected void clearSpans(boolean backgroundSpans, boolean foregroundSpans) {
        if (backgroundSpans) { //remove background color spans
            BackgroundColorSpan spans[] = getText().getSpans(0, getText().length(), BackgroundColorSpan.class);
            for (BackgroundColorSpan span : spans) {
                getText().removeSpan(span);
            }
        }
       /* if(foregroundSpans) {
            SyntaxHighlightSpan[] spans = getText().getSpans(0, getText().length(), SyntaxHighlightSpan.class);
            for (SyntaxHighlightSpan span : spans) {
                getText().removeSpan(span);
            }
        }*/
    }

    public enum CodeType {
        HTML, CSS, JS, GRADLE, JSON, XML, JAVA
    }

    /**
     * Listens to when text is changed
     */
    public interface OnTextChangedListener {
        void onTextChanged(String text);
    }

    public class CMenu implements OnCreateContextMenuListener {

        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

            //Do Something , Like:

            menu.add(0, 1, 0, "copy");
            menu.add(0, 2, 0, "paste");
        }
    }

    private class WaveSpan implements LineBackgroundSpan {

        private int lineWidth;
        private int waveSize;
        private int color;

        public WaveSpan(int color) {
            this(color, 1, 2);
        }

        public WaveSpan(int color, int lineWidth, int waveSize) {
            float scale = getResources().getDisplayMetrics().density;
            this.lineWidth = (int) (lineWidth * scale + 0.5f);
            this.waveSize = (int) (waveSize * scale + 0.5f);
            this.color = color;
        }

        @Override
        public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom, CharSequence text, int start, int end, int lnum) {
            Paint p = new Paint(paint);
            p.setColor(color);
            p.setStrokeWidth(lineWidth);
            int width = (int) paint.measureText(text, start, end);
            int doubleWaveSize = waveSize * 2;
            for (int i = left; i < width; i += doubleWaveSize) {
                canvas.drawLine(i, bottom, i + waveSize, bottom - waveSize, p);
                canvas.drawLine(i + waveSize, bottom - waveSize, i + doubleWaveSize, bottom, p);
            }
        }
    }


}
