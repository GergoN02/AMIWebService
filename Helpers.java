package uk.ac.mmu.advprog.hackathon;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Helpers {

	/**
	 * Formats time to ISO 8601 standard
	 * 
	 * @param time_raw Unchecked time input as String
	 * @return Formatted time as String
	 */
	public static String checkTime(String time_raw) {

		try {

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime res_time = LocalDateTime.parse(time_raw, dtf);

			return res_time.toString();

		} catch (DateTimeParseException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Replaces ASCII space character (+) with Unicode space
	 * 
	 * @param time_raw Non-sanitized time input as String
	 * @return Time format with correct Unicode spaces as String
	 */
	public static String sanitizeURL(String time_raw) {

		String fixedTime = time_raw.replace('+', ' ');

		return fixedTime;

	}

}
