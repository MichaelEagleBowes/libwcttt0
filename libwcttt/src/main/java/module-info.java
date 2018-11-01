module wcttt.lib {
	requires java.xml.bind;
	requires javafx.base;

	exports wcttt.lib.model;
	exports wcttt.lib.binder;
	exports wcttt.lib.algorithms;
	exports wcttt.lib.algorithms.tabu_based_memetic_approach;
	exports wcttt.lib.util;

	opens wcttt.lib.model to java.xml.bind;
}
