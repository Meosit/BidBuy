package by.mksn.epam.mentalaid.command.impl;

import by.mksn.epam.mentalaid.command.Command;
import by.mksn.epam.mentalaid.command.exception.CommandException;
import by.mksn.epam.mentalaid.command.resource.PathManager;
import by.mksn.epam.mentalaid.entity.Question;
import by.mksn.epam.mentalaid.entity.User;
import by.mksn.epam.mentalaid.service.QuestionService;
import by.mksn.epam.mentalaid.service.exception.QuestionServiceException;
import by.mksn.epam.mentalaid.service.exception.ServiceException;
import by.mksn.epam.mentalaid.service.factory.ServiceFactory;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Locale;

import static by.mksn.epam.mentalaid.command.resource.Constants.*;
import static by.mksn.epam.mentalaid.util.NullUtil.isNull;

public class EditQuestionCommand implements Command {

    private static final Logger logger = Logger.getLogger(EditQuestionCommand.class);
    private static final String QUESTION_TITLE_PARAMETER = "question_title";
    private static final String QUESTION_DESCRIPTION_PARAMETER = "question_description";
    private static final String QUESTION_ID_PARAMETER = "question_id";
    private static final String SUCCESS_VALUE_NAME = "modifiedAt";

    private static void setNotFoundResponse(HttpServletRequest request) {
        request.setAttribute(AJAX_IS_RESULT_SUCCESS_ATTRIBUTE, false);
        request.setAttribute(ERROR_TITLE_ATTRIBUTE, ERROR_TITLE_QUESTION_NOT_FOUND);
        request.setAttribute(ERROR_MESSAGE_ATTRIBUTE, ERROR_MESSAGE_QUESTION_NOT_FOUND);
    }

    private static void setAccessDeniedResponse(HttpServletRequest request) {
        request.setAttribute(AJAX_IS_RESULT_SUCCESS_ATTRIBUTE, false);
        request.setAttribute(ERROR_TITLE_ATTRIBUTE, ERROR_TITLE_ACCESS_DENIED);
        request.setAttribute(ERROR_MESSAGE_ATTRIBUTE, ERROR_MESSAGE_ACCESS_DENIED);
    }

    private static String formatDateTime(Timestamp timestamp, String locale) {
        return DateFormat
                .getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, new Locale(locale))
                .format(timestamp);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        String titleParameter = request.getParameter(QUESTION_TITLE_PARAMETER);
        String descriptionParameter = request.getParameter(QUESTION_DESCRIPTION_PARAMETER);
        String idParameter = request.getParameter(QUESTION_ID_PARAMETER);

        try {
            long quid = Long.parseLong(idParameter);
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute(USER_ATTRIBUTE);
            if (!isNull(user)) {
                QuestionService questionService = ServiceFactory.getInstance().getQuestionService();
                Question question = questionService.getById(quid);
                if (!isNull(question)) {
                    if (question.getCreatorId() == user.getId() || user.getRole() == User.ROLE_ADMIN) {
                        question.setTitle(titleParameter);
                        question.setDescription(descriptionParameter);
                        questionService.update(question);
                        request.setAttribute(AJAX_IS_RESULT_SUCCESS_ATTRIBUTE, true);
                        request.setAttribute(AJAX_SUCCESS_VALUE_NAME_ATTRIBUTE, SUCCESS_VALUE_NAME);
                        request.setAttribute(AJAX_SUCCESS_VALUE_ATTRIBUTE, formatDateTime(
                                question.getModifiedAt(),
                                (String) session.getAttribute(LOCALE_ATTRIBUTE)
                        ));
                    } else {
                        logger.warn("User '" + user.getUsername() +
                                "' trying to edit question (id=" + idParameter + ") without permission.");
                        setAccessDeniedResponse(request);
                    }
                } else {
                    logger.warn("Question not found (id=" + idParameter + ")");
                    setNotFoundResponse(request);
                }
            } else {
                logger.warn("Unauthorized user trying to edit question.\n" +
                        "(questionID=" + idParameter + "; " +
                        "newTitle=" + titleParameter + "; " +
                        "newDescription=" + descriptionParameter + ")");
                setAccessDeniedResponse(request);
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid question ID parameter passed (" + idParameter + ")");
            setNotFoundResponse(request);
        } catch (QuestionServiceException e) {
            request.setAttribute(AJAX_IS_RESULT_SUCCESS_ATTRIBUTE, false);
            request.setAttribute(ERROR_TITLE_ATTRIBUTE, ERROR_TITLE_QUESTION_WRONG_INPUT);
            request.setAttribute(ERROR_MESSAGE_ATTRIBUTE, ERROR_MESSAGE_QUESTION_WRONNG_INPUT);
        } catch (ServiceException e) {
            throw new CommandException(e);
        }

        String pagePath = PathManager.getProperty(PathManager.AJAX_RESPONSE);
        Command.dispatchRequest(pagePath, true, request, response);
    }

}