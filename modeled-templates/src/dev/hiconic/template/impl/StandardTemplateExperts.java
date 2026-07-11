package dev.hiconic.template.impl;

import com.braintribe.model.generic.reflection.SimpleTypes;

import dev.hiconic.template.api.TemplateExpertRegistry;
import dev.hiconic.template.impl.node.ArgumentedNodeEvaluator;
import dev.hiconic.template.impl.node.CommentNodeEvaluator;
import dev.hiconic.template.impl.node.DeclareInstructionEvaluator;
import dev.hiconic.template.impl.node.ErrorNodeEvaluator;
import dev.hiconic.template.impl.node.ForEachEvaluator;
import dev.hiconic.template.impl.node.IfEvaluator;
import dev.hiconic.template.impl.node.InvokeInstructionEvaluator;
import dev.hiconic.template.impl.node.OutputNodeEvaluator;
import dev.hiconic.template.impl.node.SequenceNodeEvaluator;
import dev.hiconic.template.impl.node.SetEvaluator;
import dev.hiconic.template.impl.node.SwitchEvaluator;
import dev.hiconic.template.impl.node.TextNodeEvaluator;
import dev.hiconic.template.impl.node.VarEvaluator;
import dev.hiconic.template.impl.transform.CssEscaper;
import dev.hiconic.template.impl.transform.FormatDateTransformer;
import dev.hiconic.template.impl.transform.FormatNumberTransformer;
import dev.hiconic.template.impl.transform.HtmlEscaper;
import dev.hiconic.template.impl.transform.JavaLiteralEscaper;
import dev.hiconic.template.impl.transform.JavaScriptEscaper;
import dev.hiconic.template.impl.transform.JsonEscaper;
import dev.hiconic.template.impl.transform.NoEscapeTransformer;
import dev.hiconic.template.impl.transform.UrlComponentEscaper;
import dev.hiconic.template.impl.transform.XmlEscaper;
import dev.hiconic.template.impl.vd.AddEvaluator;
import dev.hiconic.template.impl.vd.AndEvaluator;
import dev.hiconic.template.impl.vd.ConcatEvaluator;
import dev.hiconic.template.impl.vd.DivideEvaluator;
import dev.hiconic.template.impl.vd.EqEvaluator;
import dev.hiconic.template.impl.vd.GeEvaluator;
import dev.hiconic.template.impl.vd.GtEvaluator;
import dev.hiconic.template.impl.vd.LeEvaluator;
import dev.hiconic.template.impl.vd.LikeEvaluator;
import dev.hiconic.template.impl.vd.LtEvaluator;
import dev.hiconic.template.impl.vd.MultiplyEvaluator;
import dev.hiconic.template.impl.vd.NeEvaluator;
import dev.hiconic.template.impl.vd.NotEvaluator;
import dev.hiconic.template.impl.vd.OrEvaluator;
import dev.hiconic.template.impl.vd.SubtractEvaluator;
import dev.hiconic.template.impl.vd.TransformValueEvaluator;
import dev.hiconic.template.model.core.ArgumentedNode;
import dev.hiconic.template.model.core.CommentNode;
import dev.hiconic.template.model.core.ErrorNode;
import dev.hiconic.template.model.core.OutputNode;
import dev.hiconic.template.model.core.SequenceNode;
import dev.hiconic.template.model.core.TextNode;
import dev.hiconic.template.model.core.decl.DeclareInstruction;
import dev.hiconic.template.model.core.decl.Var;
import dev.hiconic.template.model.core.instr.ForEach;
import dev.hiconic.template.model.core.instr.If;
import dev.hiconic.template.model.core.instr.InvokeInstruction;
import dev.hiconic.template.model.core.instr.Set;
import dev.hiconic.template.model.core.instr.Switch;
import dev.hiconic.template.model.core.output.CssEscape;
import dev.hiconic.template.model.core.output.CssOutput;
import dev.hiconic.template.model.core.output.FormatDate;
import dev.hiconic.template.model.core.output.FormatNumber;
import dev.hiconic.template.model.core.output.HtmlEsc;
import dev.hiconic.template.model.core.output.HtmlOutput;
import dev.hiconic.template.model.core.output.JavaLiteralEscape;
import dev.hiconic.template.model.core.output.JavaLiteralOutput;
import dev.hiconic.template.model.core.output.JavaScriptEscape;
import dev.hiconic.template.model.core.output.JavaScriptOutput;
import dev.hiconic.template.model.core.output.JsonEscape;
import dev.hiconic.template.model.core.output.JsonOutput;
import dev.hiconic.template.model.core.output.NoEscape;
import dev.hiconic.template.model.core.output.RawOutput;
import dev.hiconic.template.model.core.output.UrlComponentEscape;
import dev.hiconic.template.model.core.output.UrlComponentOutput;
import dev.hiconic.template.model.core.output.XmlEscape;
import dev.hiconic.template.model.core.output.XmlOutput;
import dev.hiconic.template.model.core.vd.Add;
import dev.hiconic.template.model.core.vd.And;
import dev.hiconic.template.model.core.vd.Concat;
import dev.hiconic.template.model.core.vd.Divide;
import dev.hiconic.template.model.core.vd.Eq;
import dev.hiconic.template.model.core.vd.Ge;
import dev.hiconic.template.model.core.vd.Gt;
import dev.hiconic.template.model.core.vd.Le;
import dev.hiconic.template.model.core.vd.Like;
import dev.hiconic.template.model.core.vd.Lt;
import dev.hiconic.template.model.core.vd.Multiply;
import dev.hiconic.template.model.core.vd.Ne;
import dev.hiconic.template.model.core.vd.Not;
import dev.hiconic.template.model.core.vd.Or;
import dev.hiconic.template.model.core.vd.Subtract;
import dev.hiconic.template.model.core.vd.TransformValue;

public final class StandardTemplateExperts {
	private StandardTemplateExperts() {
	}

	public static void register(TemplateExpertRegistry registry) {
		registerHtml(registry);
	}

	public static void registerBase(TemplateExpertRegistry registry) {
		registerNodes(registry);
		registerTransformers(registry);
		registerValueDescriptors(registry);
	}

	public static void registerHtml(TemplateExpertRegistry registry) {
		registerBase(registry);
		registerHtmlDefaults(registry);
	}

	public static void registerXml(TemplateExpertRegistry registry) {
		registerBase(registry);
		registerXmlDefaults(registry);
	}

	public static void registerJson(TemplateExpertRegistry registry) {
		registerBase(registry);
		registerJsonDefaults(registry);
	}

	public static void registerCss(TemplateExpertRegistry registry) {
		registerBase(registry);
		registerCssDefaults(registry);
	}

	public static void registerJavaScript(TemplateExpertRegistry registry) {
		registerBase(registry);
		registerJavaScriptDefaults(registry);
	}

	public static void registerUrlComponent(TemplateExpertRegistry registry) {
		registerBase(registry);
		registerUrlComponentDefaults(registry);
	}

	public static void registerHtmlDefaults(TemplateExpertRegistry registry) {
		registerDateDefaults(registry);
		registry.registerDefaultTransformer(HtmlEsc.T, SimpleTypes.TYPE_STRING, HtmlOutput.T, new HtmlEscaper());
	}

	public static void registerXmlDefaults(TemplateExpertRegistry registry) {
		registerDateDefaults(registry);
		registry.registerDefaultTransformer(XmlEscape.T, SimpleTypes.TYPE_STRING, XmlOutput.T, new XmlEscaper());
	}

	public static void registerJsonDefaults(TemplateExpertRegistry registry) {
		registerDateDefaults(registry);
		registry.registerDefaultTransformer(JsonEscape.T, SimpleTypes.TYPE_STRING, JsonOutput.T, new JsonEscaper());
	}

	public static void registerCssDefaults(TemplateExpertRegistry registry) {
		registerDateDefaults(registry);
		registry.registerDefaultTransformer(CssEscape.T, SimpleTypes.TYPE_STRING, CssOutput.T, new CssEscaper());
	}

	public static void registerJavaScriptDefaults(TemplateExpertRegistry registry) {
		registerDateDefaults(registry);
		registry.registerDefaultTransformer(JavaScriptEscape.T, SimpleTypes.TYPE_STRING, JavaScriptOutput.T, new JavaScriptEscaper());
	}

	public static void registerUrlComponentDefaults(TemplateExpertRegistry registry) {
		registerDateDefaults(registry);
		registry.registerDefaultTransformer(UrlComponentEscape.T, SimpleTypes.TYPE_STRING, UrlComponentOutput.T, new UrlComponentEscaper());
	}

	private static void registerNodes(TemplateExpertRegistry registry) {
		registry.registerEvaluator(TextNode.T, new TextNodeEvaluator());
		registry.registerEvaluator(CommentNode.T, new CommentNodeEvaluator());
		registry.registerEvaluator(ErrorNode.T, new ErrorNodeEvaluator());
		registry.registerEvaluator(SequenceNode.T, new SequenceNodeEvaluator());
		registry.registerEvaluator(OutputNode.T, new OutputNodeEvaluator());
		registry.registerEvaluator(ArgumentedNode.T, new ArgumentedNodeEvaluator());
		registry.registerEvaluator(If.T, new IfEvaluator());
		registry.registerEvaluator(ForEach.T, new ForEachEvaluator());
		registry.registerEvaluator(Set.T, new SetEvaluator());
		registry.registerEvaluator(Var.T, new VarEvaluator());
		registry.registerEvaluator(DeclareInstruction.T, new DeclareInstructionEvaluator());
		registry.registerEvaluator(Switch.T, new SwitchEvaluator());
		registry.registerEvaluator(InvokeInstruction.T, new InvokeInstructionEvaluator());
	}

	private static void registerTransformers(TemplateExpertRegistry registry) {
		registry.registerTransformer(HtmlEsc.T, SimpleTypes.TYPE_STRING, HtmlOutput.T, new HtmlEscaper());
		registry.registerTransformer(XmlEscape.T, SimpleTypes.TYPE_STRING, XmlOutput.T, new XmlEscaper());
		registry.registerTransformer(JavaLiteralEscape.T, SimpleTypes.TYPE_STRING, JavaLiteralOutput.T, new JavaLiteralEscaper());
		registry.registerTransformer(JavaScriptEscape.T, SimpleTypes.TYPE_STRING, JavaScriptOutput.T, new JavaScriptEscaper());
		registry.registerTransformer(JsonEscape.T, SimpleTypes.TYPE_STRING, JsonOutput.T, new JsonEscaper());
		registry.registerTransformer(CssEscape.T, SimpleTypes.TYPE_STRING, CssOutput.T, new CssEscaper());
		registry.registerTransformer(UrlComponentEscape.T, SimpleTypes.TYPE_STRING, UrlComponentOutput.T, new UrlComponentEscaper());
		registry.registerTransformer(NoEscape.T, SimpleTypes.TYPE_STRING, RawOutput.T, new NoEscapeTransformer());
		registry.registerTransformer(FormatDate.T, SimpleTypes.TYPE_DATE, SimpleTypes.TYPE_STRING, new FormatDateTransformer());
		registerNumberTransformers(registry, false);
	}

	private static void registerDateDefaults(TemplateExpertRegistry registry) {
		registry.registerDefaultTransformer(FormatDate.T, SimpleTypes.TYPE_DATE, SimpleTypes.TYPE_STRING, new FormatDateTransformer());
		registerNumberTransformers(registry, true);
	}

	private static void registerNumberTransformers(TemplateExpertRegistry registry, boolean defaultConversion) {
		FormatNumberTransformer transformer = new FormatNumberTransformer();
		if (defaultConversion) {
			registry.registerDefaultTransformer(FormatNumber.T, SimpleTypes.TYPE_INTEGER, SimpleTypes.TYPE_STRING, transformer);
			registry.registerDefaultTransformer(FormatNumber.T, SimpleTypes.TYPE_LONG, SimpleTypes.TYPE_STRING, transformer);
			registry.registerDefaultTransformer(FormatNumber.T, SimpleTypes.TYPE_FLOAT, SimpleTypes.TYPE_STRING, transformer);
			registry.registerDefaultTransformer(FormatNumber.T, SimpleTypes.TYPE_DOUBLE, SimpleTypes.TYPE_STRING, transformer);
			registry.registerDefaultTransformer(FormatNumber.T, SimpleTypes.TYPE_DECIMAL, SimpleTypes.TYPE_STRING, transformer);
		} else {
			registry.registerTransformer(FormatNumber.T, SimpleTypes.TYPE_INTEGER, SimpleTypes.TYPE_STRING, transformer);
			registry.registerTransformer(FormatNumber.T, SimpleTypes.TYPE_LONG, SimpleTypes.TYPE_STRING, transformer);
			registry.registerTransformer(FormatNumber.T, SimpleTypes.TYPE_FLOAT, SimpleTypes.TYPE_STRING, transformer);
			registry.registerTransformer(FormatNumber.T, SimpleTypes.TYPE_DOUBLE, SimpleTypes.TYPE_STRING, transformer);
			registry.registerTransformer(FormatNumber.T, SimpleTypes.TYPE_DECIMAL, SimpleTypes.TYPE_STRING, transformer);
		}
	}

	private static void registerValueDescriptors(TemplateExpertRegistry registry) {
		registry.registerVdEvaluator(And.T, new AndEvaluator());
		registry.registerVdEvaluator(Or.T, new OrEvaluator());
		registry.registerVdEvaluator(Not.T, new NotEvaluator());
		registry.registerVdEvaluator(Eq.T, new EqEvaluator());
		registry.registerVdEvaluator(Ne.T, new NeEvaluator());
		registry.registerVdEvaluator(Gt.T, new GtEvaluator());
		registry.registerVdEvaluator(Ge.T, new GeEvaluator());
		registry.registerVdEvaluator(Lt.T, new LtEvaluator());
		registry.registerVdEvaluator(Le.T, new LeEvaluator());
		registry.registerVdEvaluator(Like.T, new LikeEvaluator());
		registry.registerVdEvaluator(Concat.T, new ConcatEvaluator());
		registry.registerVdEvaluator(Add.T, new AddEvaluator());
		registry.registerVdEvaluator(Subtract.T, new SubtractEvaluator());
		registry.registerVdEvaluator(Multiply.T, new MultiplyEvaluator());
		registry.registerVdEvaluator(Divide.T, new DivideEvaluator());
		registry.registerVdEvaluator(TransformValue.T, new TransformValueEvaluator(registry));
	}
}
