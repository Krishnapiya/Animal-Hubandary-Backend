/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.keltron.utility.constants.logging;

/**
 *
 * @author krishnapriya
 *
 */
public enum LogState {
	BEGIN {
		@Override
		public String getPrefix() {
			return "Execution begin for method" + LogConstant.STRING_SEPERATOR;
		}
	},
	END {
		@Override
		public String getPrefix() {
			return "Execution complete for method" + LogConstant.STRING_SEPERATOR;
		}
	},
	EXCEPTION {
		@Override
		public String getPrefix() {
			return "Exception occured for method" + LogConstant.STRING_SEPERATOR;
		}
	},
	INFO {
		@Override
		public String getPrefix() {
			return "Method info" + LogConstant.STRING_SEPERATOR;
		}
	},
	OUTPUT {
		@Override
		public String getPrefix() {
			return "Producing" + LogConstant.STRING_SEPERATOR;
		}
	},
	INPUT {
		@Override
		public String getPrefix() {
			return "Consuming" + LogConstant.STRING_SEPERATOR;
		}
	},
	INTERMEDIATE {
		@Override
		public String getPrefix() {
			return "Intermediate print" + LogConstant.STRING_SEPERATOR;
		}
	},
	WARNING {
		@Override
		public String getPrefix() {
			return "Raising warning" + LogConstant.STRING_SEPERATOR;
		}
	},
	ERROR {
		@Override
		public String getPrefix() {
			return "Raising error" + LogConstant.STRING_SEPERATOR;
		}
	},
	BAD_REQUEST {
		@Override
		public String getPrefix() {
			return "Wrong input parameters" + LogConstant.STRING_SEPERATOR;
		}
	},
	NOTICE {
		@Override
		public String getPrefix() {
			return "Method notice" + LogConstant.STRING_SEPERATOR;
		}
	},
	LOCAL_VARIALBE {
		@Override
		public String getPrefix() {
			return "Method local variable" + LogConstant.STRING_SEPERATOR;
		}
	},
	CLASS_VARIALBE {
		@Override
		public String getPrefix() {
			return "Class variable" + LogConstant.STRING_SEPERATOR;
		}
	},
	ALERT {
		@Override
		public String getPrefix() {
			return "Raising alert" + LogConstant.STRING_SEPERATOR;
		}
	};

	/**
	 * Returns the logger string for this log state
	 *
	 * @return The logger string
	 */
	public abstract String getPrefix();
}