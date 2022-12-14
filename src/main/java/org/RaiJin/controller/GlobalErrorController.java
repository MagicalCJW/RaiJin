package org.RaiJin.controller;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.protocol.SentryId;
import org.RaiJin.common.config.ItachiProps;
import org.RaiJin.common.env.EnvConfig;
import org.RaiJin.exception.RaiJinNoAuthException;
import org.RaiJin.view.ErrorPage;
import org.RaiJin.view.ErrorPageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.net.SocketTimeoutException;

@Controller
@SuppressWarnings("Duplicates")
public class GlobalErrorController implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorController.class);

    @Autowired
    ErrorPageFactory errorPageFactory;

    @Autowired
    ItachiProps itachiProps;

    @Autowired
    EnvConfig envConfig;

//    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        ErrorPage errorPage = null;
        if (exception instanceof RaiJinNoAuthException) {
            errorPage = errorPageFactory.buildForbiddenErrorPage();
        } else if (exception instanceof ResourceAccessException resourceAccessException) {
            if (resourceAccessException.contains(SocketTimeoutException.class)) {
                errorPage = errorPageFactory.buildTimeoutErrorPage();
            }
        }

        if (errorPage == null) {
            errorPage = errorPageFactory.buildInternalServerErrorPage();
        }

        if (exception != null) {
            if (envConfig.isDebug())
                log.error("Global error handling " + exception);
            else {
                Sentry.captureException((Exception)exception);
                SentryId uuid = Sentry.getLastEventId();
                errorPage.setSentryErrorId(uuid.toString());
                errorPage.setSentryPublicDsn(itachiProps.getSentryDsn());
                log.warn("reported error to sentry " + "id " + uuid + " error " + exception);
            }
        }

        model.addAttribute("page", errorPage);

        return "error";
    }
}

