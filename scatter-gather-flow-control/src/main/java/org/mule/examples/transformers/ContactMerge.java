/**
 * MuleSoft Examples
 * Copyright 2014 MuleSoft, Inc.
 *
 * This product includes software developed at
 * MuleSoft, Inc. (http://www.mulesoft.com/).
 */

package org.mule.examples.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * The object of this class will take two lists as input and create a third one that will be the merge of the previous two. The identity of an element of the list is defined by its email.
 */
public class ContactMerge {

	/**
	 * The method will merge the contacts from the two lists creating a new one.
	 * 
	 * @param contactsFromA
	 *            leads from organization A
	 * @param contactsFromB
	 *            leads from organization B
	 * @return a list with the merged content of the to input lists
	 */
	public List<Map<String, String>> mergeList(List<Map<String, String>> contactsFromA, List<Map<String, String>> contactsFromB) {
		List<Map<String, String>> mergedContactList = new ArrayList<Map<String, String>>();

		// Put all contacts from A in the merged mergedleadsList
		for (Map<String, String> leadFromA : contactsFromA) {
			Map<String, String> mergedContact = createMergedContact(leadFromA);
			mergedContact.put("IDInA", leadFromA.get("Id"));
			mergedContactList.add(mergedContact);
		}

		// Add the new contacts from B and update the exiting ones
		for (Map<String, String> contact : contactsFromB) {
			Map<String, String> leadFromA = findContactInList(contact.get("Email"), mergedContactList);
			if (leadFromA != null) {
				leadFromA.put("IDInB", contact.get("Id"));
			}
			else {
				Map<String, String> mergedContact = createMergedContact(contact);
				mergedContact.put("IDInB", contact.get("Id"));
				mergedContactList.add(mergedContact);
			}
		}
		return mergedContactList;
	}

	private Map<String, String> createMergedContact(Map<String, String> lead) {
		Map<String, String> mergedLead = new HashMap<String, String>();
		mergedLead.put("Name", lead.get("Name"));
		mergedLead.put("Email", lead.get("Email"));
		mergedLead.put("IDInA", "");
		mergedLead.put("IDInB", "");
		return mergedLead;
	}

	private Map<String, String> findContactInList(String contactEmail, List<Map<String, String>> contactsList) {
		if(StringUtils.isBlank(contactEmail))
			return null;
		for (Map<String, String> contact : contactsList) {
			if (StringUtils.isNotBlank(contact.get("Email")) && contact.get("Email").equals(contactEmail)) {
				return contact;
			}
		}
		return null;
	}
}
