package by.mksn.epam.mentalaid.command.impl;

import by.mksn.epam.mentalaid.command.Command;
import by.mksn.epam.mentalaid.command.exception.CommandException;
import by.mksn.epam.mentalaid.command.resource.PathManager;
import by.mksn.epam.mentalaid.entity.Question;
import by.mksn.epam.mentalaid.service.QuestionService;
import by.mksn.epam.mentalaid.service.exception.ServiceException;
import by.mksn.epam.mentalaid.service.factory.ServiceFactory;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static by.mksn.epam.mentalaid.command.resource.Constants.*;
import static by.mksn.epam.mentalaid.util.StringUtil.isNullOrEmpty;

/**
 * Returns to client home page
 */
public class GetHomePageCommand implements Command {

    private static final Logger logger = Logger.getLogger(GetHomePageCommand.class);

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        String pageParameter = request.getParameter(PAGE_INDEX_PARAMETER);
        String searchQuery = request.getParameter(SEARCH_QUERY_PARAMETER);

        int pageIndex = 1;
        if (!isNullOrEmpty(pageParameter)) {
            try {
                pageIndex = Integer.parseInt(pageParameter);
                if ((pageIndex - 1) * QuestionService.QUESTIONS_PER_PAGE < 0) {
                    pageIndex = 1;
                }
            } catch (NumberFormatException e) {
                logger.trace("Invalid page parameter passed (" + pageParameter + ")");
            }
        }

        List<Question> questions;
        int pageCount;
        try {
            QuestionService questionService = ServiceFactory.getInstance().getQuestionService();
            if (isNullOrEmpty(searchQuery)) {
                pageCount = questionService.getPageCount();
                questions = questionService.getQuestionsPage(pageIndex);
            } else {
                pageCount = questionService.getSearchPageCount(searchQuery);
                questions = questionService.getSearchQuestionsPage(searchQuery, pageIndex);
            }
        } catch (ServiceException e) {
            throw new CommandException(e);
        }

        request.setAttribute(CURRENT_PAGE_ATTRIBUTE, pageIndex);
        request.setAttribute(QUESTION_LIST_ATTRIBUTE, questions);
        request.setAttribute(PAGE_COUNT_ATTRIBUTE, pageCount);

        String pagePath = PathManager.getProperty(PathManager.HOME);
        Command.dispatchRequest(pagePath, false, request, response);
    }

}
