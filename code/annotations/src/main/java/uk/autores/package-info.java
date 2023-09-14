/**
 * <h2>AUTORES</h2>
 *
 * An annotation driven library for working with classpath resource files.
 *
 * <h3>Usage</h3>
 *
 * To process a resource annotate a class or package with {@link uk.autores.ClasspathResource}
 * and set fields as appropriate for the specified {@link uk.autores.processing.Handler}.
 *
 * <h3>Resource Handlers</h3>
 *
 * <table border="1">
 *     <caption>provided handlers</caption>
 *     <thead>
 *         <tr>
 *             <th>{@link uk.autores.processing.Handler}</th>
 *             <th>Resources</th>
 *             <th>Configuration</th>
 *             <th>Description</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>{@link uk.autores.AssertResourceExists}</td>
 *             <td>any files</td>
 *             <td><em>(none)</em></td>
 *             <td>Default {@link uk.autores.processing.Handler} that validates resources exist.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.GenerateByteArraysFromFiles}</td>
 *             <td>any files</td>
 *             <td>{@link uk.autores.ConfigDefs#VISIBILITY}</td>
 *             <td>Generates a class with methods that return resources as byte arrays.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.GenerateConstantsFromProperties}</td>
 *             <td>{@link java.util.Properties} files</td>
 *             <td>{@link uk.autores.ConfigDefs#VISIBILITY}</td>
 *             <td>Generates classes containing String constants of the keys in a properties file.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.GenerateMessagesFromProperties}</td>
 *             <td>{@link java.util.Properties} files</td>
 *             <td>{@link uk.autores.ConfigDefs#VISIBILITY} {@link uk.autores.ConfigDefs#LOCALIZE} {@link uk.autores.ConfigDefs#MISSING_KEY} {@link uk.autores.ConfigDefs#FORMAT}</td>
 *             <td>Generates classes with methods for returning property values including support for localization and message formatting.</td>
 *         </tr>
 *         <tr>
 *             <td>{@link uk.autores.GenerateStringsFromText}</td>
 *             <td>text files</td>
 *             <td>{@link uk.autores.ConfigDefs#VISIBILITY} {@link uk.autores.ConfigDefs#ENCODING}</td>
 *             <td>Default {@link uk.autores.processing.Handler} that validates resources exist.</td>
 *         </tr>
 *     </tbody>
 * </table>
 *
 * <h3>Links</h3>
 *
 * <ul>
 *     <li><a href="https://github.com/autores-uk/autores">Source code</a></li>
 *     <li><a href="https://autores.uk">Website</a></li>
 * </ul>
 */
package uk.autores;
