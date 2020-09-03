package com.zu.musicscoretest.music_score;

import android.view.ViewGroup;

import com.zu.musicxml3_0.Appearance;
import com.zu.musicxml3_0.Defaults;
import com.zu.musicxml3_0.MarginType;
import com.zu.musicxml3_0.PageLayout;
import com.zu.musicxml3_0.PageMargins;
import com.zu.musicxml3_0.Scaling;
import com.zu.musicxml3_0.ScorePartwise;
import com.zu.musicxml3_0.ScoreTimewise;
import com.zu.musicxml3_0.StaffLayout;
import com.zu.musicxml3_0.SystemDividers;
import com.zu.musicxml3_0.SystemLayout;
import com.zu.musicxml3_0.SystemMargins;
import com.zu.musicxml3_0.Work;
import com.zu.musicxml3_0.opus.Opus;

import java.io.InputStream;
import java.util.List;

public class MusicScore {

    public static float LINE_SPACE = 6.0f;
    public static float TRACK_SPACE = 40.0f;


    public static float DPI = 300; // 印刷行业标准

    // 1英寸=25.4毫米
    public static float MM_TO_INCH = 25.4f;



    // 毫米，默认为A4标准尺寸，竖向。
    public static float DEFAULT_PAGE_WIDTH_MM = 210;
    public static float DEFAULT_PAGE_HEIGHT_MM = 297;

    public static float DEFAULT_V_MARGIN = 15;
    public static float DEFAULT_H_MARGIN = 15;

    private float MMtoPX = DPI / MM_TO_INCH;

    private float scalingValue = 0;


    private float pageWidth = 0;
    private float pageHeight = 0;


    // PageLayout
    private MarginType marginType = null;

    private Margin pageOddMargin = new Margin();
    private Margin pageEvenMargin = new Margin();



    // SystemLayout
    private Margin systemMargin = new Margin();
    private float systemDistance = 0f;
    private float topSystemDistance = 0f;
    private SystemDividers systemDividers = null;
    private List<StaffLayout> staffLayouts = null;

    // appearance
    Appearance appearance = null;


    public enum Type{
        PARTWISE,
        TIMEWISE,
        OPUS,
        UNKNOWN
    }

    private Type type = Type.UNKNOWN;

    private Object score = null;



    public MusicScore()
    {

    }

    public boolean load(InputStream is)
    {
        boolean result = true;
        try{
            score = Unmarshall.unmarshall(is);
            if(score instanceof ScorePartwise)
            {
                type = Type.PARTWISE;
                result = analysePartwise();
            }else if(score instanceof ScoreTimewise)
            {
                type = Type.TIMEWISE;
                result = analyseTimewise();
            }else if(score instanceof Opus)
            {
                type = Type.OPUS;
            }else
            {
                type = Type.UNKNOWN;
                result = false;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            result = false;
        }
        return result;

    }

    private boolean analysePartwise()
    {
        if(score == null || !(score instanceof ScorePartwise))
        {
            return false;
        }
        ScorePartwise scoreP = (ScorePartwise)score;
        boolean result = true;
        if(scoreP.getWork() != null)
        {
            analyseDefaults(scoreP.getDefaults());
        }
        return result;
    }

    private boolean analyseTimewise()
    {
        if(score == null || !(score instanceof ScoreTimewise))
        {
            return false;
        }
        ScoreTimewise scoreT = (ScoreTimewise)score;
        boolean result = true;

        if(scoreT.getDefaults() != null)
        {
            analyseDefaults(scoreT.getDefaults());
        }

        return result;
    }

    // Defaults包含界面布局信息
    private void analyseDefaults(Defaults defaults)
    {
        if(defaults == null)
        {
            return;
        }
        resolveScaling(defaults.getScaling());

        resolvePageLayout(defaults.getPageLayout());

        resolveSystemLayout(defaults.getSystemLayout());

        resolveStaffLayout(defaults.getStaffLayout());

        resolveAppearance(defaults.getAppearance());


    }

    private void resolveScaling(Scaling scaling)
    {
        if(scaling == null)
        {
            scalingValue = 4.0f / 2.5f;
        }
        else
        {
            scalingValue = scaling.getMillimeters().floatValue() / scaling.getTenths().floatValue();
        }
    }

    private void resolvePageLayout(PageLayout pageLayout)
    {
        if(pageLayout != null && pageLayout.getPageHeight() != null)
        {
            pageHeight = pageLayout.getPageHeight().floatValue() * scalingValue * MMtoPX;
            pageWidth = pageLayout.getPageWidth().floatValue() * scalingValue * MMtoPX;
        }
        else
        {
            pageWidth = DEFAULT_PAGE_WIDTH_MM * MMtoPX;
            pageHeight = DEFAULT_PAGE_HEIGHT_MM * MMtoPX;
        }

        if(pageLayout != null && pageLayout.getPageMargins() != null && pageLayout.getPageMargins().size() != 0)
        {
            for(PageMargins margins : pageLayout.getPageMargins())
            {
                switch (margins.getType())
                {
                    case BOTH:
                    {
                        pageOddMargin.leftMargin = pageEvenMargin.leftMargin = margins.getLeftMargin().floatValue() * scalingValue * MMtoPX;
                        pageOddMargin.rightMargin = pageEvenMargin.rightMargin = margins.getRightMargin().floatValue() * scalingValue * MMtoPX;
                        pageOddMargin.topMargin = pageEvenMargin.topMargin = margins.getTopMargin().floatValue() * scalingValue * MMtoPX;
                        pageOddMargin.bottomMargin = pageEvenMargin.bottomMargin = margins.getBottomMargin().floatValue() * scalingValue * MMtoPX;
                    }
                    break;
                    case ODD:
                    {
                        pageOddMargin.leftMargin = margins.getLeftMargin().floatValue() * scalingValue * MMtoPX;
                        pageOddMargin.rightMargin = margins.getRightMargin().floatValue() * scalingValue * MMtoPX;
                        pageOddMargin.topMargin = margins.getTopMargin().floatValue() * scalingValue * MMtoPX;
                        pageOddMargin.bottomMargin = margins.getBottomMargin().floatValue() * scalingValue * MMtoPX;
                    }
                    break;
                    case EVEN:
                    {
                        pageEvenMargin.leftMargin = margins.getLeftMargin().floatValue() * scalingValue * MMtoPX;
                        pageEvenMargin.rightMargin = margins.getRightMargin().floatValue() * scalingValue * MMtoPX;
                        pageEvenMargin.topMargin = margins.getTopMargin().floatValue() * scalingValue * MMtoPX;
                        pageEvenMargin.bottomMargin = margins.getBottomMargin().floatValue() * scalingValue * MMtoPX;
                    }
                    break;
                    default:
                        break;
                }
            }
        }
        else
        {
            pageOddMargin.leftMargin = pageEvenMargin.leftMargin = DEFAULT_H_MARGIN * MMtoPX;
            pageOddMargin.rightMargin = pageEvenMargin.rightMargin = DEFAULT_H_MARGIN * MMtoPX;
            pageOddMargin.topMargin = pageEvenMargin.topMargin = DEFAULT_V_MARGIN * MMtoPX;
            pageOddMargin.bottomMargin = pageEvenMargin.bottomMargin = DEFAULT_V_MARGIN * MMtoPX;
        }
    }

    private void resolveSystemLayout(SystemLayout systemLayout)
    {
        if(systemLayout != null && systemLayout.getSystemMargins() != null)
        {
            SystemMargins sm = systemLayout.getSystemMargins();
            systemMargin.leftMargin = sm.getLeftMargin().floatValue() * scalingValue * MMtoPX;
            systemMargin.rightMargin = sm.getRightMargin().floatValue() * scalingValue * MMtoPX;
        }
        else
        {
            systemMargin.leftMargin = 0f;
            systemMargin.rightMargin = 0f;
        }

        if(systemLayout != null && systemLayout.getSystemDistance() != null)
        {
            systemDistance = systemLayout.getSystemDistance().floatValue() * scalingValue * MMtoPX;
        }
        else
        {
            systemDistance = 0f;
        }

        topSystemDistance = systemLayout == null ? 0f : systemLayout.getTopSystemDistance().floatValue() * scalingValue * MMtoPX;

        systemDividers = systemLayout == null ? null : systemLayout.getSystemDividers();
    }

    private void resolveStaffLayout(List<StaffLayout> staffLayouts)
    {
        this.staffLayouts = staffLayouts;
    }

    private void resolveAppearance(Appearance appearance)
    {
        this.appearance = appearance;
    }






}
