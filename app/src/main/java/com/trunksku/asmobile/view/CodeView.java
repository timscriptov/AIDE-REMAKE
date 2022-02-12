package com.trunksku.asmobile.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeView extends AppCompatAutoCompleteTextView {

    public static final String ERROR_COLOR = "#ff0000";
    public static final String WARNING_COLOR = "#E69317";
    private static final Pattern PATTERN_LINE = Pattern.compile("(^.+$)+", Pattern.MULTILINE);
    private static final Pattern PATTERN_TRAILING_WHITE_SPACE = Pattern.compile("[\\t ]+$", Pattern.MULTILINE);
    private final Handler mUpdateHandler = new Handler();
    private final float displayDensity = getResources().getDisplayMetrics().density;
    private final SortedMap<Integer, Integer> mErrorHashSet = new TreeMap<>();
    private final Map<Pattern, Integer> mSyntaxPatternMap = new HashMap<>();
    // private final Context context;
    Paint highlightPaint = new Paint();
    Paint mPaint = new Paint();
    private int tabWidth;
    private int tabWidthInCharacters;
    private int mUpdateDelayTime = 500;
    private boolean modified = true;
    private final Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            Editable source = getText();
            highlightWithoutChange(source);
        }
    };
    private boolean highlightWhileTextChanging = true;
    private boolean hasErrors = false;
    private boolean mRemoveErrorsWhenTextChanged = true;
    private final TextWatcher mEditorTextWatcher = new TextWatcher() {

        private int start;
        private int count;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            this.start = start;
            this.count = count;
        }

        @Override
        public void onTextChanged(CharSequence pcharSequence, int start, int before, int count) {
            if (!modified) return;

            if (highlightWhileTextChanging) {
                if (mSyntaxPatternMap.size() > 0) {
                    convertTabs(getEditableText(), start, count);
                    mUpdateHandler.postDelayed(mUpdateRunnable, mUpdateDelayTime);
                }
            }

            if (mRemoveErrorsWhenTextChanged) removeAllErrorLines();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!highlightWhileTextChanging) {
                if (!modified) return;

                cancelHighlighterRender();

                if (mSyntaxPatternMap.size() > 0) {
                    convertTabs(getEditableText(), start, count);
                    mUpdateHandler.postDelayed(mUpdateRunnable, mUpdateDelayTime);
                }
            }
        }
    };
    private MultiAutoCompleteTextView.Tokenizer mAutoCompleteTokenizer;
    private List<Character> mIndentCharacterList = Arrays.asList('{', '+', '-', '*', '/', '=');
    private Rect rect;
    private Paint paint;

    public CodeView(Context context) {
        super(context);
        initEditorView();
    }

    public CodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initEditorView();
    }

    public CodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initEditorView();
    }

    public static void clearSpans(Spannable spannable, Class<?> spanClass, int start_pos, int end_pos) {
        for (Object span : spannable.getSpans(start_pos, end_pos, spanClass)) {
            spannable.removeSpan(span);
        }
    }

    private void initEditorView() {
        setBackgroundColor(Color.TRANSPARENT);
        setHorizontallyScrolling(true);
        //setEnabled(false);
       /* rect = new Rect();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        paint.setTextSize(getTextSize());*/
        //paint.setTypeface(Typeface.MONOSPACE);
        mPaint.setColor(Color.GRAY);
        mPaint.setTypeface(Typeface.MONOSPACE);
        setTextSize(11);
        if (mAutoCompleteTokenizer == null) {
            mAutoCompleteTokenizer = new KeywordTokenizer();
        }

        // setTokenizer(mAutoCompleteTokenizer);
        //setHorizontallyScrolling(true);
        setGravity(Gravity.TOP);
        setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        if (modified &&
                                end - start == 1 &&
                                start < source.length() &&
                                dstart < dest.length()) {
                            char c = source.charAt(start);

                            if (c == '\n') {
                                return autoIndent(source, dest, dstart, dend);
                            }
                        }
                        return source;
                    }
                }
        });

        addTextChangedListener(mEditorTextWatcher);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /*int baseline;
        int lineCount = getLineCount();
        int lineNumber = 1;

        for (int i = 0; i < lineCount; ++i)
        {
            baseline=getLineBounds(i, null);
            if (i == 0)
            {
                canvas.drawText(""+lineNumber, rect.left, baseline, paint);
                ++lineNumber;
            }
            else if (getText().charAt(getLayout().getLineStart(i) - 1) == '\n')
            {
                canvas.drawText(""+lineNumber, rect.left, baseline, paint);
                ++lineNumber;
            }
        }

        if(lineCount<100)
        {
            setPadding(25,getPaddingTop(),getPaddingRight(),getPaddingBottom());
        }
        else if(lineCount>99 && lineCount<1000)
        {
            setPadding(43,getPaddingTop(),getPaddingRight(),getPaddingBottom());
        }
        else if(lineCount>999 && lineCount<10000)
        {
            setPadding(53,getPaddingTop(),getPaddingRight(),getPaddingBottom());
        }
        else if(lineCount>9999 && lineCount<100000)
        {
            setPadding(63,getPaddingTop(),getPaddingRight(),getPaddingBottom());
        }*/
        mDraw(canvas);
        super.onDraw(canvas);
    }

    @Override
    public int getCompoundPaddingLeft() {
        int symbol_offset = (int) (getTextSize() / 1.5);
        return Integer.toString(getLineCount()).length() * symbol_offset + symbol_offset;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if (!hasFocus())
            requestFocus();

       /* if(!pasting_second) {
            insertSecondChar(text, start, lengthBefore, lengthAfter);
            cutBigString(text);
        } else {
            pasting_second = false;
        }*/
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        mPaint.setTextSize(getTextSize());
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        mPaint.setTextSize(getTextSize());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (hasFocus() && getSelectionStart() == getSelectionEnd())
            clearFocus();
        performClick();
        return super.onTouchEvent(event);
    }

    private CharSequence autoIndent(CharSequence source, Spanned dest, int dstart, int dend) {
        String indent = "";
        int istart = dstart - 1;

        boolean dataBefore = false;
        int pt = 0;

        for (; istart > -1; --istart) {
            char c = dest.charAt(istart);

            if (c == '\n') break;

            if (c != ' ' && c != '\t') {
                if (!dataBefore) {
                    if (mIndentCharacterList.contains(c)) --pt;
                    dataBefore = true;
                }

                if (c == '(') {
                    --pt;
                } else if (c == ')') {
                    ++pt;
                }
            }
        }

        if (istart > -1) {
            char charAtCursor = dest.charAt(dstart);
            int iend;

            for (iend = ++istart; iend < dend; ++iend) {
                char c = dest.charAt(iend);

                if (charAtCursor != '\n' &&
                        c == '/' &&
                        iend + 1 < dend &&
                        dest.charAt(iend) == c) {
                    iend += 2;
                    break;
                }

                if (c != ' ' && c != '\t') {
                    break;
                }
            }

            indent += dest.subSequence(istart, iend);
        }

        if (pt < 0) {
            indent += "\t";
        }

        return source + indent;
    }

    private void highlightLine(int line, int color, Canvas canvas) {
        Rect lineBounds = new Rect();
        highlightPaint.setColor(color);
        getLineBounds(line, lineBounds);
        lineBounds.left = 0;
        canvas.drawRect(lineBounds, highlightPaint);
    }

    private void highlightSyntax(Editable editable) {
        if (mSyntaxPatternMap.isEmpty()) return;

        for (Pattern pattern : mSyntaxPatternMap.keySet()) {
            int color = mSyntaxPatternMap.get(pattern);
            for (Matcher m = pattern.matcher(editable); m.find(); ) {
                createForegroundColorSpan(editable, m, color);
            }
        }
    }

    private void highlightErrorLines(Editable editable) {
        if (mErrorHashSet.isEmpty()) return;
        int maxErrorLineValue = mErrorHashSet.lastKey();

        int lineNumber = 0;
        Matcher matcher = PATTERN_LINE.matcher(editable);
        while (matcher.find()) {
            if (mErrorHashSet.containsKey(lineNumber)) {
                int color = mErrorHashSet.get(lineNumber);
                createBackgroundColorSpan(editable, matcher, color);
            }
            lineNumber = lineNumber + 1;
            if (lineNumber > maxErrorLineValue) break;
        }
    }

    private void createForegroundColorSpan(Editable editable, Matcher matcher, int color) {
        editable.setSpan(new ForegroundColorSpan(color),
                matcher.start(), matcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void createBackgroundColorSpan(Editable editable, Matcher matcher, int color) {
        editable.setSpan(new BackgroundColorSpan(color),
                matcher.start(), matcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private Editable highlight(Editable editable) {
        if (editable.length() == 0) return editable;

        try {
            clearSpans(editable);
            highlightErrorLines(editable);
            highlightSyntax(editable);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return editable;
    }

    private void highlightWithoutChange(Editable editable) {
        modified = false;
        highlight(editable);
        modified = true;
    }

    public void setTextHighlighted(CharSequence text) {
        if (text == null || text.length() == 0) return;

        cancelHighlighterRender();

        removeAllErrorLines();

        modified = false;
        setText(highlight(new SpannableStringBuilder(text)));
        modified = true;
    }

    public void setTabWidth(int characters) {
        if (tabWidthInCharacters == characters) return;
        tabWidthInCharacters = characters;
        tabWidth = Math.round(getPaint().measureText("m") * characters);
    }

    private void clearSpans(Editable editable) {
        int length = editable.length();
        ForegroundColorSpan[] foregroundSpans = editable.getSpans(
                0, length, ForegroundColorSpan.class);

        for (int i = foregroundSpans.length; i-- > 0; )
            editable.removeSpan(foregroundSpans[i]);

        BackgroundColorSpan[] backgroundSpans = editable.getSpans(
                0, length, BackgroundColorSpan.class);

        for (int i = backgroundSpans.length; i-- > 0; )
            editable.removeSpan(backgroundSpans[i]);
    }

    public void cancelHighlighterRender() {
        mUpdateHandler.removeCallbacks(mUpdateRunnable);
    }

    private void convertTabs(Editable editable, int start, int count) {
        if (tabWidth < 1) return;

        String s = editable.toString();

        for (int stop = start + count;
             (start = s.indexOf("\t", start)) > -1 && start < stop;
             ++start) {
            editable.setSpan(new CodeView.TabWidthSpan(),
                    start,
                    start + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public void setSyntaxPatternsMap(Map<Pattern, Integer> syntaxPatterns) {
        if (!mSyntaxPatternMap.isEmpty()) mSyntaxPatternMap.clear();
        mSyntaxPatternMap.putAll(syntaxPatterns);
    }

    public void addSyntaxPattern(Pattern pattern, int Color) {
        mSyntaxPatternMap.put(pattern, Color);
    }

    public void removeSyntaxPattern(Pattern pattern) {
        mSyntaxPatternMap.remove(pattern);
    }

    public int getSyntaxPatternsSize() {
        return mSyntaxPatternMap.size();
    }

    public void resetSyntaxPatternList() {
        mSyntaxPatternMap.clear();
    }

    public void clearAutoIndentCharacterList() {
        mIndentCharacterList.clear();
    }

    public List<Character> getAutoIndentCharacterList() {
        return mIndentCharacterList;
    }

    public void setAutoIndentCharacterList(List<Character> characterList) {
        mIndentCharacterList = characterList;
    }

    public void addErrorLine(int lineNum, int color) {
        mErrorHashSet.put(lineNum, color);
        hasErrors = true;
    }

    public void removeErrorLine(int lineNum) {
        mErrorHashSet.remove(lineNum);
        hasErrors = mErrorHashSet.size() > 0;
    }

    public void removeAllErrorLines() {
        mErrorHashSet.clear();
        hasErrors = false;
    }

    public int getErrorsSize() {
        return mErrorHashSet.size();
    }

    public String getTextWithoutTrailingSpace() {
        return PATTERN_TRAILING_WHITE_SPACE
                .matcher(getText())
                .replaceAll("");
    }

    public void setAutoCompleteTokenizer(MultiAutoCompleteTextView.Tokenizer tokenizer) {
        mAutoCompleteTokenizer = tokenizer;
    }

    public void setRemoveErrorsWhenTextChanged(boolean removeErrors) {
        mRemoveErrorsWhenTextChanged = removeErrors;
    }

    public void reHighlightSyntax() {
        highlightSyntax(getEditableText());
    }

    public void reHighlightErrors() {
        highlightErrorLines(getEditableText());
    }

    public boolean isHasError() {
        return hasErrors;
    }

    public int getUpdateDelayTime() {
        return mUpdateDelayTime;
    }

    public void setUpdateDelayTime(int time) {
        mUpdateDelayTime = time;
    }

    public void setHighlightWhileTextChanging(boolean updateWhileTextChanging) {
        this.highlightWhileTextChanging = updateWhileTextChanging;
    }

    private void mDraw(Canvas canvas) {
        Layout layout = getLayout();
        if (layout != null) {
            int current_line = getCurrentLine();
            // highlighting current line
            highlightLine(current_line, Color.GRAY, canvas);
            // drawing line numbers
            int count = getLineCount();

            for (int i = 0; i < count; i++) {
                int line_baseline = getLineBounds(i, null);
                String num = Integer.toString(i + 1);
                canvas.drawText(num, 10 + getScrollX(), line_baseline, mPaint);
            }
            // drawing vertical divider
            /*int x = getScrollX() + getCompoundPaddingLeft() - 10;
            int y = Math.max(getLineHeight() * getLineCount(), getHeight()) + getExtendedPaddingBottom();
            canvas.drawLine(x, 0, x, y, mPaint);*/
        }
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

    public void setError(int start, int end) {
        getText().setSpan(new WaveSpan(Color.parseColor(ERROR_COLOR)), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }

   /* @Override
    public void showDropDown() {
        int[] screenPoint = new int[2];
        getLocationOnScreen(screenPoint);

        final Rect displayFrame = new Rect();
        getWindowVisibleDisplayFrame(displayFrame);

        int position = getSelectionStart();
        Layout layout = getLayout();
        int line = layout.getLineForOffset(position);

        float verticalDistanceInDp = (750 + 140 * line) / displayDensity;
        setDropDownVerticalOffset((int)verticalDistanceInDp);

        float horizontalDistanceInDp = layout.getPrimaryHorizontal(position) / displayDensity;
        setDropDownHorizontalOffset((int) horizontalDistanceInDp);
        super.showDropDown();
    }*/

    public void setWarning(int start, int end) {
        getText().setSpan(new WaveSpan(Color.parseColor(WARNING_COLOR)), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }

    public void clearErrors() {
        clearSpans(getText(), WaveSpan.class, 0, getText().length());
    }

    public int getCurrentLine() {
        Layout l = getLayout();
        return l == null ? -1 : l.getLineForOffset(getSelectionStart());
    }

    public class KeywordTokenizer implements MultiAutoCompleteTextView.Tokenizer {

        @Override
        public int findTokenStart(CharSequence charSequence, int cursor) {
            String sequenceStr = charSequence.toString();
            sequenceStr = sequenceStr.substring(0, cursor);

            int spaceIndex = sequenceStr.lastIndexOf(" ");
            int lineIndex = sequenceStr.lastIndexOf("\n");
            int bracketIndex = sequenceStr.lastIndexOf("(");

            int index = Math.max(0, Math.max(spaceIndex, Math.max(lineIndex, bracketIndex)));
            if (index == 0) return 0;
            return (index + 1 < charSequence.length()) ? index + 1 : index;
        }

        @Override
        public int findTokenEnd(CharSequence charSequence, int cursor) {
            return charSequence.length();
        }

        @Override
        public CharSequence terminateToken(CharSequence charSequence) {
            return charSequence;
        }

    }

    private final class TabWidthSpan extends ReplacementSpan {

        @Override
        public int getSize(
                Paint paint,
                CharSequence text,
                int start,
                int end,
                Paint.FontMetricsInt fm) {
            return tabWidth;
        }

        @Override
        public void draw(
                Canvas canvas,
                CharSequence text,
                int start,
                int end,
                float x,
                int top,
                int y,
                int bottom,
                Paint paint) {
        }
    }

    private class WaveSpan implements LineBackgroundSpan {

        private int lineWidth;
        private int waveSize;
        private int color;

        WaveSpan(int color) {
            this(color, 1, 3);
        }

        WaveSpan(int color, int lineWidth, int waveSize) {
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


