# JGame Engine

## Quick Setup
To begin creating in JGame start by adding your code to the GameInstance class in the Project Package. There should only be one Game Instance script, and it will work as your Main script where you can start adding objects into the world.


In the initialization function create a new instance of Window and provide the size and fullscreen configuration or leave it blank to use borderless fullscreen.

Then you can start using the engine callbacks like start or update, create objects, adding components and creating your game!

To create custom behavior for components create new classes extending from JComponent and add them to your objects.

## Settings
You can access engine settings in the Setting class found in the engine package, here you can find values to set up the engine to your game's needs.

## Features

### 3D Rendering

#### Meshes
Meshes hold 3D models to be displayed by the Mesh Renderer Component
#### Billboards
Similar to Mesh Renderers, there is a Billboard Renderer component that can be used to display 2D images as billboards.
#### Textures
Textures can be added to renderers to change the visual display of models or billboards.
#### Shaders
Custom shaders are supported by Materials, the shaders are written in glsl and can be used to create unique visual styles for your game
#### Material
The materials hold basic information for the renderers, including textures, shaders and more.
### Physics Simulations
The engine supports a rigidbody-based physics simulator, using the Rigidbody components you can add collisions and physics to your game.
### Hierarchy-Based Object-Oriented Engine
This engine is based on a parent-child hierarchy of objects.
Each object "JGameObject" has "JComponents", and a "Transform".

The objects can have parents and transforms are updated accordingly to their local positions relative to their parents, allowing for complex combinations of objects.
### Main Classes

#### JGameObject
##### Overview
A JGameObject is an object with position that exists in the world. It can hold JComponents and is the simplest building block for your game.

##### Most Important Functions

#### JComponent
##### Overview
A JComponent is an abstract class that represents a piece of code that will be executed alongside an object. It can be attached to a JGameObject, you can create your own classes extending from JComponent to create custom behavior.
##### Most Important Functions


## Execution Order
This section describes de execution order for different sections of objects and engine processes, divided in different moments. Some parts are executed instantaneously after Creating the object, others are called during the engine's update loop.

The section that runs in engine loop ensures that every object gets the callback before advancing to the next callback, for example, before calling Update for any object Physics Update will be called for all objects.
The other sections do not guarantee the same behavior, as they are called right after their specified conditions, meaning that the order depends on the specific execution of your code.
1. Right after instantiation
   1. Initialize
   2. OnEnable
2. Right after changing Activeness
    1. OnEnable/OnDisable
3. When calling Destroy
    1. OnDisable
    2. OnDestroy
    3. Delete the object
4. In Engine Loop
   1. Start
   2. Every Frame:
      1. Early Update
      2. Physics Update
      3. Rendering
      4. Update
      5. Late Update