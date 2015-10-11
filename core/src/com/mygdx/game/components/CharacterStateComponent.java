package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.mygdx.game.input.IntentBroadcast;

/**
 * Created by Johannes Sjolund on 10/11/15.
 */
public class CharacterStateComponent implements Component {

	public StateMachine<CharacterStateComponent> stateMachine;
	public AnimationController controller;

	public CharacterState moveState = CharacterState.MOVE_WALK;
	public CharacterState idleState = CharacterState.IDLE_STAND;

	public PathFindingComponent pathCmp;
	public ModelComponent mdlCmp;
	public IntentBroadcast intentCmp;
	public SelectableComponent selCmp;
	public RagdollComponent ragdollCmp;
	public PhysicsComponent phyCmp;
	public MotionStateComponent motionCmp;

	public boolean isMoving = false;

	public CharacterStateComponent(IntentBroadcast intentCmp,
								   ModelComponent mdlCmp,
								   PathFindingComponent pathCmp,
								   SelectableComponent selCmp,
								   RagdollComponent ragdollCmp,
								   PhysicsComponent phyCmp,
								   MotionStateComponent motionCmp) {
		this.intentCmp = intentCmp;
		this.mdlCmp = mdlCmp;
		this.motionCmp = motionCmp;
		this.pathCmp = pathCmp;
		this.phyCmp = phyCmp;
		this.ragdollCmp = ragdollCmp;
		this.selCmp = selCmp;

		controller = new AnimationController(mdlCmp.modelInstance);
		stateMachine = new DefaultStateMachine<CharacterStateComponent>(this, CharacterState.IDLE_STAND);
		stateMachine.changeState(CharacterState.IDLE_STAND);
	}

	public void update(float delta) {
		stateMachine.update();
		controller.update(delta);

		if (pathCmp.goalReached && isMoving) {
			stateMachine.changeState(idleState);
		} else if (!pathCmp.goalReached && !isMoving) {
			stateMachine.changeState(moveState);
		}

		if (intentCmp.killSelected() && selCmp.isSelected) {
			if (stateMachine.getCurrentState() == CharacterState.DEAD) {
				stateMachine.changeState(CharacterState.IDLE_STAND);
			} else {
				stateMachine.changeState(CharacterState.DEAD);
			}
		}
	}


}