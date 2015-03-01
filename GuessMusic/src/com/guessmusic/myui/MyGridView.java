package com.guessmusic.myui;

import java.util.ArrayList;

import com.guessmusic.R;
import com.guessmusic.model.IWordButtonClickListener;
import com.guessmusic.model.WordButton;
import com.guessmusic.util.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class MyGridView extends GridView {
	public final static int COUNTS_WORDS = 24;

	private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();

	private MyGridAdapter mAdapter;

	private Context mContext;

	private Animation mScaleAnimation;

	private IWordButtonClickListener mWordButtonListener;

	public MyGridView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);

		mContext = context;

		mAdapter = new MyGridAdapter();
		this.setAdapter(mAdapter);
	}

	public void updateData(ArrayList<WordButton> list) {
		mArrayList = list;

		// ������������Դ
		setAdapter(mAdapter);
	}

	class MyGridAdapter extends BaseAdapter {
		public int getCount() {
			return mArrayList.size();
		}

		public Object getItem(int pos) {
			return mArrayList.get(pos);
		}

		public long getItemId(int pos) {
			return pos;
		}

		public View getView(int pos, View v, ViewGroup p) {
			final WordButton holder;

			if (v == null) {
				v = Util.getView(mContext, R.layout.self_ui_gridview_item);

				holder = mArrayList.get(pos);

				// ���ض���
				mScaleAnimation = AnimationUtils.loadAnimation(mContext,
						R.anim.scale);

				// ���ö������ӳ�ʱ��
				mScaleAnimation.setStartOffset(pos * 100);

				holder.mIndex = pos;

				if (holder.mViewButton == null) {
					holder.mViewButton = (Button) v.findViewById(R.id.item_btn);
					holder.mViewButton
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									// �ص�
									mWordButtonListener
											.onWordButtonClick(holder);
								}
							});
				}

				v.setTag(holder);
			} else {
				holder = (WordButton) v.getTag();
			}

			holder.mViewButton.setText(holder.mWordString);

			// ���Ŷ���
			v.startAnimation(mScaleAnimation);

			return v;
		}
	}

	public void registOnWordButtonClick(IWordButtonClickListener listener) {
		mWordButtonListener = listener;
	}
}
