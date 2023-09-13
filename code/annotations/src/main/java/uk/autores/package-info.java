/**
 * <h1>Autores</h1>
 *
 * An annotation driven library for working with classpath resource files.
 *
 * <h2>Usage</h2>
 *
 * To process a resource annotate a class or package with {@link uk.autores.ClasspathResource}
 * and set fields as appropriate for the specified {@link uk.autores.Handler}.
 *
 * <h2>Resource Handlers</h2>
 *
 * <table border="1">
 *     <caption>Library functionality</caption>
 *     <thead>
 *         <tr>
 *             <th>{@link uk.autores.Handler}</th>
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
 *             <td>Default {@link uk.autores.Handler} that validates resources exist.</td>
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
 *             <td>Default {@link uk.autores.Handler} that validates resources exist.</td>
 *         </tr>
 *     </tbody>
 * </table>
 *
 * <h2>Links</h2>
 *
 * <ul>
 *     <li><a href="https://github.com/autores-uk/autores">Source code</a></li>
 *     <li><a href="https://autores.uk">Website</a></li>
 * </ul>
 */
package uk.autores;
