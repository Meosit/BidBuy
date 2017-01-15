package by.mksn.epam.mentalaid.tag;

import org.apache.log4j.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.SkipPageException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * Tag to format price, more simple and shorter than fmt:numberFormat
 */
public class BootstrapPaginationBlockTag extends SimpleTagSupport {

    private static final Logger logger = Logger.getLogger(BootstrapPaginationBlockTag.class);

    private static final String BLOCK_HEADER = "<nav aria-label='Page navigation'><ul class='pagination'>";
    private static final String BLOCK_FOOTER = "</ul></nav>";
    private static final String BLOCK_PAGE_NUMBER_TEMPLATE = "<li #active><a href='#url'>#number</a></li>";
    private static final String BLOCK_PREV_TEMPLATE = "<li #visible><a href='#url' aria-label='Previous'><span aria-hidden='true'>&larr;</span></a></li>";
    private static final String BLOCK_NEXT_TEMPLATE = "<li #visible><a href='#url' aria-label='Next'><span aria-hidden='true'>&rarr;</span></a></li>";
    private static final String BLOCK_NUMBER_RANGE_PLACEHOLDER = "<li><a>...</a></li>";

    private String baseUrl;
    private int currentPageIndex;
    private int pageCount;

    @Override
    public void doTag() throws JspException, IOException {
        try {
            JspWriter out = getJspContext().getOut();
            if (pageCount != 1) {
                out.write(BLOCK_HEADER);
                out.write(getPrevBlock());
                out.write(getPageNumberBlock(1));
                if (pageCount != 1) {
                    if (currentPageIndex >= 4) {
                        if (currentPageIndex == 4) {
                            out.write(getPageNumberBlock(2));
                        } else {
                            out.write(BLOCK_NUMBER_RANGE_PLACEHOLDER);
                        }
                    }

                    out.write(getCurrentPageBlockAndNearest());

                    if (pageCount - currentPageIndex >= 3) {
                        if (pageCount - currentPageIndex == 3) {
                            out.write(getPageNumberBlock(pageCount - 1));
                        } else {
                            out.write(BLOCK_NUMBER_RANGE_PLACEHOLDER);
                        }
                    }
                    out.write(getPageNumberBlock(pageCount));
                }
                out.write(getNextBlock());
                out.write(BLOCK_FOOTER);
            }
        } catch (Exception e) {
            logger.error("Cannot generate pagination block", e);
            throw new SkipPageException("Exception in generating pagination block. ", e);
        }
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPageIndex = currentPage;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    private String getCurrentPageBlockAndNearest() {
        String result = "";
        if (currentPageIndex > 2) {
            result += getPageNumberBlock(currentPageIndex - 1);
        }
        if (currentPageIndex != pageCount && currentPageIndex != 1) {
            result += getPageNumberBlock(currentPageIndex);
        }
        if (currentPageIndex < pageCount - 1) {
            result += getPageNumberBlock(currentPageIndex + 1);
        }
        return result;
    }

    private String getPageNumberBlock(int pageIndex) {
        String block = BLOCK_PAGE_NUMBER_TEMPLATE.replace("#number", pageIndex + "");
        block = block.replace("#active", (currentPageIndex == pageIndex) ? "class='active'" : "");
        return block.replace("#url", baseUrl + pageIndex);
    }

    private String getPrevBlock() {
        String url = baseUrl + ((currentPageIndex == 1) ? 1 : (currentPageIndex - 1));
        String block = BLOCK_PREV_TEMPLATE.replace("#url", url);
        block = block.replace("#visible", (currentPageIndex == 1) ? "class='invisible'" : "");
        return block;
    }

    private String getNextBlock() {
        String url = baseUrl + ((currentPageIndex == pageCount) ? pageCount : (currentPageIndex + 1));
        String block = BLOCK_NEXT_TEMPLATE.replace("#url", url);
        block = block.replace("#visible", (currentPageIndex == pageCount) ? "class='invisible'" : "");
        return block;
    }

}
