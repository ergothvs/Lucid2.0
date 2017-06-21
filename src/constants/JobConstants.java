/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package constants;

/**
 *
 * @author Itzik
 */
public class JobConstants {

	public static final boolean enableJobs = true;
	public static final int jobOrder = 8;

	public enum LoginJob {

		RESISTANCE(0, JobFlag.ENABLED),
		EXPLORER(1, JobFlag.ENABLED),
		CYGNUS(2, JobFlag.ENABLED),
		ARAN(3, JobFlag.ENABLED),
		EVAN(4, JobFlag.ENABLED),
		MERCEDES(5, JobFlag.ENABLED),
		DEMON(6, JobFlag.ENABLED),
		PHANTOM(7, JobFlag.ENABLED),
		DUAL_BLADE(8, JobFlag.ENABLED),
		MIHILE(9, JobFlag.ENABLED),
		LUMINOUS(10, JobFlag.ENABLED),
		KAISER(11, JobFlag.ENABLED),
		ANGELIC(12, JobFlag.ENABLED),
		CANNONEER(13, JobFlag.ENABLED),
		XENON(14, JobFlag.ENABLED),
		ZERO(15, JobFlag.ENABLED),
		SHADE(16, JobFlag.ENABLED),
		JETT(17, JobFlag.ENABLED),
		HAYATO(18, JobFlag.ENABLED),
		KANNA(19, JobFlag.ENABLED),
		CHASE(20, JobFlag.ENABLED),
		PINK_BEAN(21, JobFlag.DISABLED),
		KINESIS(22, JobFlag.ENABLED);

		private final int jobType, flag;

		private LoginJob(int jobType, JobFlag flag) {
			this.jobType = jobType;
			this.flag = flag.getFlag();
		}

		public int getJobType() {
			return jobType;
		}

		public int getFlag() {
			return flag;
		}

		public enum JobFlag {

			DISABLED(0), 
			ENABLED(1),
			HUNDREDPLUS(2); // requires level 100+ char
			private final int flag;

			private JobFlag(int flag) {
				this.flag = flag;
			}

			public int getFlag() {
				return flag;
			}
		}
	}
}
