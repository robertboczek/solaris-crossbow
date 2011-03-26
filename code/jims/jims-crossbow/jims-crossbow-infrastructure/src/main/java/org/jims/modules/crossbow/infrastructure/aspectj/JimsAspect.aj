package org.jims.modules.crossbow.infrastructure.aspectj;

import org.jims.modules.crossbow.infrastructure.worker.Worker;

public aspect JimsAspect {

	pointcut myClass() : within( Worker );
	pointcut addMethod() : myClass() && execution(* instantiateADD(..)) ;
	pointcut remMethod() : myClass() && execution(* instantiateREM(..)) ;
	pointcut updMethod() : myClass() && execution(* instantiateUPD(..)) ;

	before (): addMethod() {
		System.out.println("Before");
	}

	after (): addMethod() {
		System.out.println("After");
	}

	before (): remMethod() {
		System.out.println("Before");
	}

	after (): remMethod() {
		System.out.println("After");
	}

	before (): updMethod() {
		System.out.println("Before");
	}

	after (): updMethod() {
		System.out.println("After");
	}

}
