# 419-Libraries

To start using this project download or clone the repository. The jars included in the `lib/` folder need
to be included in your projects classpath for it to be able to use the JUNG2 libraries, and the DOM4J to
parse graphml files. Some of the jars are dependencies of others which is why there are quite a few.

# Development Style

Please keep classes simple, and preferably static (this is not always possible and is up to me to enforce moving
forward with the other components of the system, see GraphML loader or PathBasedComponents). A class should 
implement a few basic public methods which the users would be able to invoke. Classes should preferably be generic 
for vertices and edges, however if your algorithm requires setting attributes for vertices or edges then you can use
`<V extends Vertex, E extends Edge>` where Vertex and Edge are in the `core.components` package. There
are no dependencies between classes in this library, except for the few interfaces that are implemented.
You can feel free to use any classes/methods within the library if they will help you implement your algorithm.
Therefore you should be free to create your algorithms without needing to rely on what has already been
developed. (Of course, you can use the generators to create random graphs to test your algorithm on, or
the graph loaders.) Try to include your algorithm in a package that is related to its functionality, or
you can create a new package if your algorithm does not fit with the provided packages.

If you need to develop an extension of the JUNG2 Graph class for your implementation you can implement
that and store the class in the `core.components` package.

# Visualization

If you would like to visualize your algorithms you can implement a `viewGraph` method in your class, similar
to what you can find in the Breadth/Depth First Search classes where you can customize the visualizer to
generate what is most appropriate for your algorithms. Extended visualizers can be included in the `core.visualizer`
package if you would like it to be available for more than a particular class.

# Documentation

A complete, up to date version of the Javadoc for this project has been generated which should explain
how the provided algorithms work and their dependencies.

A complete Javadoc for JUNG2 can be found at http://jung.sourceforge.net/doc/api/

# Contact

If you have any questions about how to use these libraries you can contact me by email at
mikenowicki022@gmail.com

If you have any suggestions for improvements to this library I would always welcome the input, feel free to
submit pull requests if you have made an improvement to the library. It would be much appreciated!