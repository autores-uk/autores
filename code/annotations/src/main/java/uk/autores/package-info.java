/**
 * <p>
 * An annotation driven library for working with classpath resource files.
 * This package contains the core library functionality.
 * See other packages for annotation processing and extensibility.
 * </p>
 * <h2>AUTORES</h2>
 *
 * <h3>Usage</h3>
 *
 * <ol>
 *     <li>
 *         Place resource files in the appropriate place for the chosen build system.
 *         <ul><li>
 *             In Apache Maven this is typically under
 *             <a href="https://maven.apache.org/plugins/maven-resources-plugin/examples/resource-directory.html">
 *             <code>src/main/resources</code>
 *             </a>
 *          </li></ul>
 *     </li>
 *     <li>
 *         Annotate a type or package declaration with {@link uk.autores.ClasspathResource}.
 *     </li>
 *     <li>
 *         Set the resource file name in the {@link uk.autores.ClasspathResource#value()} array.
 *     </li>
 *     <li>
 *         Specify a {@link uk.autores.processing.Handler} for non-default behaviour.
 *     </li>
 * </ol>
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
 *             <td>{@link uk.autores.ConfigDefs#VISIBILITY} {@link uk.autores.ConfigDefs#STRATEGY}</td>
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
 *             <td>{@link uk.autores.ConfigDefs#VISIBILITY} {@link uk.autores.ConfigDefs#ENCODING} {@link uk.autores.ConfigDefs#STRATEGY}</td>
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
