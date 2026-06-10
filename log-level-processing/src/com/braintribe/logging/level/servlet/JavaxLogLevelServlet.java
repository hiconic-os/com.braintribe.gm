package com.braintribe.logging.level.servlet;

import java.io.IOException;

import com.braintribe.gm.logging.level.LogLevelApplicationResolver;
import com.braintribe.cfg.Required;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reason;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.service.api.InstanceId;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.result.Neutral;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JavaxLogLevelServlet extends HttpServlet {
	private static final String ACTION_STATE = "state";

	private final LogLevelServletSupport delegate = new LogLevelServletSupport();

	@Required
	public void setEvaluator(Evaluator<ServiceRequest> evaluator) {
		delegate.setEvaluator(evaluator);
	}

	public void setLocalInstanceId(InstanceId localInstanceId) {
		delegate.setLocalInstanceId(localInstanceId);
	}

	public void setApplicationResolver(LogLevelApplicationResolver applicationResolver) {
		delegate.setApplicationResolver(applicationResolver);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Maybe<Neutral> result = delegate.handlePost(req.getParameter("action"), req::getParameter);

		if (result.isUnsatisfied()) {
			sendUnsatisfied(resp, result.whyUnsatisfied());
			return;
		}

		resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (ACTION_STATE.equals(req.getParameter("action"))) {
			writeState(req, resp);
			return;
		}

		writePage(resp);
	}

	private void writePage(HttpServletResponse resp) throws IOException {
		Maybe<String> html = delegate.pageHtml();
		if (html.isUnsatisfied()) {
			sendUnsatisfied(resp, html.whyUnsatisfied());
			return;
		}

		resp.setContentType("text/html;charset=UTF-8");
		resp.getWriter().print(html.get());
	}

	private void writeState(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Maybe<String> json = delegate.stateJson(req.getParameter("app"));
		if (json.isUnsatisfied()) {
			sendUnsatisfied(resp, json.whyUnsatisfied());
			return;
		}

		resp.setContentType("application/json;charset=UTF-8");
		resp.getWriter().print(json.get());
	}

	private void sendUnsatisfied(HttpServletResponse resp, Reason reason) throws IOException {
		resp.sendError(statusCode(reason), reason == null ? "Unsatisfied" : reason.stringify(true));
	}

	private int statusCode(Reason reason) {
		return reason != null && InvalidArgument.T.isInstance(reason) ? HttpServletResponse.SC_BAD_REQUEST
				: HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	}
}
