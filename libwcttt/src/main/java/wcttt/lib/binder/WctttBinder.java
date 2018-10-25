/*
 * WCT³ (WIAI Course Timetabling Tool) is a software that strives to automate
 * the timetabling process at the WIAI faculty of the University of Bamberg.
 *
 * libwcttt comprises the data model, a binder (parser + emitter) to store the
 * data as XML files, the implementations of the algorithms as well as
 * functionality to calculate conflicts and their violations.
 *
 * Copyright (C) 2018 Nicolas Gross
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package wcttt.lib.binder;

import wcttt.lib.model.Course;
import wcttt.lib.model.Semester;
import wcttt.lib.model.SemesterImpl;
import wcttt.lib.model.Session;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.net.URL;

/**
 * Provides functionality to parse/write a semester from/to a XML file.
 */
public class WctttBinder {

	private static final URL SCHEMA_FILE =
			WctttBinder.class.getResource("/wcttt-schema.xsd");

	private File xmlFile;
	private Schema schema;
	private JAXBContext context;


	public WctttBinder(File file) throws WctttBinderException {
		if (file == null) {
			throw new IllegalArgumentException("Parameter 'file' must not be " +
					"null");
		}
		this.xmlFile = file;
		SchemaFactory sf = SchemaFactory.newInstance(
					XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			this.schema = sf.newSchema(SCHEMA_FILE);
			this.context = JAXBContext.newInstance(
					SemesterImpl.class);
		} catch (JAXBException e) {
			// According to doc, thrown if there are problems with the mappings
			// -> decision for RuntimeException because there must be a serious
			// problem in the implementation
			throw new WctttBinderFatalException("Fatal problem, error in the " +
					"implementation of XML mappings, please contact the " +
					"developers", e);
		} catch (SAXException e) {
			throw new WctttBinderException("Error while parsing the schema " +
					"file", e);
		}
	}

	public File getXmlFile() {
		return xmlFile;
	}

	public Semester parse() throws WctttBinderException {
		try {
			Unmarshaller um = context.createUnmarshaller();
			um.setSchema(schema);
			Semester semester = (Semester) um.unmarshal(xmlFile);
			mapCoursesToSessions(semester);
			return semester;
		} catch (JAXBException e) {
			throw new WctttBinderException("Error while parsing a XML file", e);
		}
	}

	private void mapCoursesToSessions(Semester semester) {
		for (Course course : semester.getCourses()) {
			for (Session lecture : course.getLectures()) {
				lecture.setCourse(course);
			}
			for (Session practical : course.getPracticals()) {
				practical.setCourse(course);
			}
		}
	}

	public void write(Semester semester) throws WctttBinderException {
		if (semester == null) {
			throw new IllegalArgumentException("Parameter semester must no be" +
					"null");
		}
		try {
			Marshaller ms = context.createMarshaller();
			ms.setSchema(schema);
			ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			ms.marshal(semester, xmlFile);
		} catch (JAXBException e) {
			throw new WctttBinderException("Error while writing a XML file", e);
		}
	}

}
