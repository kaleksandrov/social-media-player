package com.kaleksandrov.smp.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.kaleksandrov.smp.util.ValidationUtils;

public class StateButton extends ImageButton {

	/* Members */

	private CycleList<State> mStates;
	private OnStateChangeListener mListener;
	private Context mContext;

	/* Constructors */

	public StateButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context.getApplicationContext();
		init();
	}

	public StateButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context.getApplicationContext();
		init();
	}

	public StateButton(Context context) {
		super(context);
		mContext = context.getApplicationContext();
		init();
	}

	/* Public methods */

	public void addState(String name, String title, int background) {
		ValidationUtils.notEmpty(name);
		ValidationUtils.notEmpty(title);
		ValidationUtils.greaterThan(background, 0);

		State state = new State(name, title, background);
		if (mStates == null) {
			mStates = new CycleList<State>(state);
		} else {
			mStates.add(state);
		}

	}

	public void addState(String name, int titleId, int background) {
		ValidationUtils.notEmpty(name);
		ValidationUtils.greaterThan(titleId, 0);
		ValidationUtils.greaterThan(background, 0);

		String title = mContext.getString(titleId);
		State state = new State(name, title, background);
		if (mStates == null) {
			mStates = new CycleList<State>(state);
			updateButtonState();
		} else {
			mStates.add(state);
		}
	}

	public void goTo(String stateName) {
		mStates.moveTo(new State(stateName, null, 0));
		updateButtonState();
	}

	public void setOnStateChangeListener(OnStateChangeListener listener) {
		ValidationUtils.notNull(listener);
		mListener = listener;
	}

	@Override
	public void setOnClickListener(OnClickListener listener) {
		throw new UnsupportedOperationException("This implementation of the Button class does not support this listener. Use OnStateChangeListener instead.");
	}

	/* Private methods */

	private void init() {
		super.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mStates == null) {
					// No states were added
					return;
				}

				State current = mStates.getCurrent();
				State next = mStates.getNext();

				if (mListener != null && mListener.onStateChange(current.mName, next.mName)) {
					return;
				}

				mStates.moveToNext();
				updateButtonState();
			}
		});
	}

	private void updateButtonState() {
		State state = mStates.getCurrent();
		setImageResource(state.mBackground);
		setContentDescription(state.mTitle);
	}

	/* Helper classes & intefaces */

	public static interface OnStateChangeListener {
		boolean onStateChange(String oldState, String newState);
	}

	private static class State {
		String mName;
		String mTitle;
		int mBackground;

		public State(String name, String title, int backgound) {
			mName = name;
			mTitle = title;
			mBackground = backgound;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((mName == null) ? 0 : mName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			State other = (State) obj;
			if (mName == null) {
				if (other.mName != null) return false;
			} else if (!mName.equals(other.mName)) return false;
			return true;
		}
	}

	private static class CycleList<T> {
		Node<T> mRoot;
		Node<T> mLast;
		Node<T> mCurrent;

		public CycleList(T data) {
			Node<T> node = new Node<T>(data);
			mRoot = node;
			mLast = node;
			mCurrent = node;
		}

		public void add(T data) {
			Node<T> node = new Node<T>(data);
			mLast.mNext = node;
			mLast = node;
			mLast.mNext = mRoot;
		}

		public T getCurrent() {
			return mCurrent.mData;
		}

		public T getNext() {
			return mCurrent.mNext.mData;
		}

		public void moveToNext() {
			mCurrent = mCurrent.mNext;
		}

		public void moveTo(T data) {
			while (!mCurrent.mData.equals(data)) {
				mCurrent = mCurrent.mNext;
			}
		}

		private static class Node<T> {
			T mData;
			Node<T> mNext;

			public Node(T data) {
				mData = data;
			}
		}
	}
}
