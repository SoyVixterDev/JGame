package JGame.Engine.Physics.General;

import JGame.Engine.Physics.Interfaces.IForceGenerator;
import JGame.Engine.Physics.Bodies.Rigidbody;

public record ForceRegistration(IForceGenerator Generator, Rigidbody Rigidbody) { }