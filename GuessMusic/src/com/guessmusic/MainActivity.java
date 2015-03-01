package com.guessmusic;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.guessmusic.R;
import com.guessmusic.data.Const;
import com.guessmusic.model.IWordButtonClickListener;
import com.guessmusic.model.Song;
import com.guessmusic.model.WordButton;
import com.guessmusic.myui.MyGridView;
import com.guessmusic.util.MyLog;
import com.guessmusic.util.MyPlayer;
import com.guessmusic.util.Util;

public class MainActivity extends Activity implements IWordButtonClickListener {

	public final static String TAG = "MainActivity";

	/** ��״̬ ���� ��ȷ */
	public final static int STATUS_ANSWER_RIGHT = 1;

	/** ��״̬ ���� ���� */
	public final static int STATUS_ANSWER_WRONG = 2;

	/** ��״̬ ���� ������ */
	public final static int STATUS_ANSWER_LACK = 3;

	// ��˸����
	public final static int SPASH_TIMES = 6;

	// ��Ƭ��ض���
	private Animation mPanAnim;
	private LinearInterpolator mPanLin;

	private Animation mBarInAnim;
	private LinearInterpolator mBarInLin;

	private Animation mBarOutAnim;
	private LinearInterpolator mBarOutLin;

	// ��Ƭ�ؼ�
	private ImageView mViewPan;
	// ���˿ؼ�
	private ImageView mViewPanBar;
	// ��ǰ������
	private TextView mCurrentStagePassView;
	private TextView mCurrentStageView;
	// ��ǰ��������
	private TextView mCurrentSongNamePassView;

	// Play �����¼�
	private ImageButton mBtnPlayStart;

	// ���ؽ���
	private View mPassView;

	// ��ǰ�����Ƿ���������
	private boolean mIsRunning = false;

	// ���ֿ�����
	private ArrayList<WordButton> mAllWords;

	private ArrayList<WordButton> mBtnSelectWords;

	private MyGridView mMyGridView;

	// ��ѡ�����ֿ�UI����
	private LinearLayout mViewWordsContainer;

	// ��ǰ�ĸ���
	private Song mCurrentSong;

	// ��ǰ�ص�����
	private int mCurrentStageIndex = -1;

	// ��ǰ�������
	private int mCurrentCoins = Const.TOTAL_COINS;

	// ���View
	private TextView mViewCurrentCoins;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// ��ʼ���ؼ�
		mViewPan = (ImageView) findViewById(R.id.imageView1);
		mViewPanBar = (ImageView) findViewById(R.id.imageView2);

		mMyGridView = (MyGridView) findViewById(R.id.gridview);

		mViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
		mViewCurrentCoins.setText(mCurrentCoins + "");

		// ע�����
		mMyGridView.registOnWordButtonClick(this);

		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);

		// ��ʼ������
		mPanAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		mPanLin = new LinearInterpolator();
		mPanAnim.setInterpolator(mPanLin);
		mPanAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// ���������˳�����
				mViewPanBar.setAnimation(mBarOutAnim);
				mIsRunning = false;
				mBtnPlayStart.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		mBarInAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
		mBarInLin = new LinearInterpolator();
		mBarInAnim.setFillAfter(true);
		mBarInAnim.setInterpolator(mBarInLin);
		mBarInAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// ��ʼ��Ƭ����
				mViewPan.startAnimation(mPanAnim);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		mBarOutAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_d_45);
		mBarOutLin = new LinearInterpolator();
		mBarOutAnim.setFillAfter(true);
		mBarOutAnim.setInterpolator(mBarOutLin);
		mBarOutAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// ���׶����������
				mIsRunning = false;
				mBtnPlayStart.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		mBtnPlayStart = (ImageButton) findViewById(R.id.btn_play_start);
		mBtnPlayStart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				handlePlayButton();
			}
		});

		// ��ʼ����Ϸ����
		initCurrentStageData();

		// ����ɾ�������¼�
		handleDeleteWord();

		// ������ʾ�����¼�
		handleTipAnswer();
	}

	@Override
	public void onWordButtonClick(WordButton wordButton) {
		// Toast.makeText(this, wordButton.mIndex + "",
		// Toast.LENGTH_SHORT).show();
		setSelectWord(wordButton);

		// ��ô�״̬
		int checkResult = checkTheAnswer();

		// ����
		if (checkResult == STATUS_ANSWER_RIGHT) {
			// ���ز���ý���
			// Toast.makeText(this, "STATUS_ANSWER_RIGHT",
			// Toast.LENGTH_SHORT).show();
			handlePassEvent();
		} else if (checkResult == STATUS_ANSWER_WRONG) {
			// ��˸���ֲ���ʾ�û�
			sparkTheWrods();
		} else if (checkResult == STATUS_ANSWER_LACK) {
			// ����������ɫΪ��ɫ��Normal��
			for (int i = 0; i < mBtnSelectWords.size(); i++) {
				mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);
			}
		}
	}

	/**
	 * ������ؽ��漰�¼�
	 */
	private void handlePassEvent() {
		// ��ʾ���ؽ���
		mPassView = (LinearLayout) this.findViewById(R.id.pass_view);
		mPassView.setVisibility(View.VISIBLE);

		// ֹͣδ��ɵĶ���
		mViewPan.clearAnimation();
		//ֹͣ���ڲ��ŵ�����
		MyPlayer.stopTheSong(MainActivity.this);
		// ��ǰ�ص�����
		mCurrentStagePassView = (TextView) findViewById(R.id.text_current_stage_pass);
		if (mCurrentStagePassView != null) {
			mCurrentStagePassView.setText((mCurrentStageIndex + 1) + "");
		}
		// ��ʾ��������
		mCurrentSongNamePassView = (TextView) findViewById(R.id.text_current_songname_pass);
		if (mCurrentSongNamePassView != null) {
			mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		}
		// ��ʾ��һ�ذ���
		ImageButton btnPass = (ImageButton) findViewById(R.id.btn_next);
		btnPass.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (judgeAppPassed()) {
					// ����ͨ�ؽ���
					Util.startActivity(MainActivity.this, AllPassView.class);
				} else {
					// �_ʼ��һ�P
					mPassView.setVisibility(View.GONE);
					// ���d�P������
					initCurrentStageData();
				}
			}
		});
	}

	/**
	 * �ж��Ƿ�ͨ��
	 * 
	 * @return
	 */
	private boolean judgeAppPassed() {
		return (mCurrentStageIndex == Const.SONG_INFO.length - 1);
	}

	private void clearTheAnswer(WordButton wordButton) {
		wordButton.mViewButton.setText("");
		wordButton.mWordString = "";
		wordButton.mIsVisiable = false;

		// ���ô�ѡ��ɼ���
		setButtonVisiable(mAllWords.get(wordButton.mIndex), View.VISIBLE);
	}

	/**
	 * ���ô�
	 * 
	 * @param wordButton
	 */
	private void setSelectWord(WordButton wordButton) {
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				// ���ô����ֿ����ݼ��ɼ���
				mBtnSelectWords.get(i).mViewButton
						.setText(wordButton.mWordString);
				mBtnSelectWords.get(i).mIsVisiable = true;
				mBtnSelectWords.get(i).mWordString = wordButton.mWordString;
				// ��¼����
				mBtnSelectWords.get(i).mIndex = wordButton.mIndex;

				MyLog.d(TAG, mBtnSelectWords.get(i).mIndex + "");

				// ���ô�ѡ��ɼ���
				setButtonVisiable(wordButton, View.INVISIBLE);

				break;
			}
		}
	}

	/**
	 * ���ô�ѡ���ֿ��Ƿ�ɼ�
	 * 
	 * @param button
	 * @param visibility
	 */
	private void setButtonVisiable(WordButton button, int visibility) {
		button.mViewButton.setVisibility(visibility);
		button.mIsVisiable = (visibility == View.VISIBLE) ? true : false;

		MyLog.d(TAG, button.mIsVisiable + "");
	}

	/**
	 * ����Բ���м�Ĳ��Ű�ť�����ǿ�ʼ��������
	 */
	private void handlePlayButton() {
		if (mViewPanBar != null) {
			if (!mIsRunning) {
				mIsRunning = true;

				// ��ʼ���˽��붯��
				mViewPanBar.startAnimation(mBarInAnim);
				mBtnPlayStart.setVisibility(View.INVISIBLE);

				// ��������
				MyPlayer.playSong(MainActivity.this,
						mCurrentSong.getSongFileName());
			}
		}
	}

	@Override
	public void onPause() {
		// ֹͣ����
		mViewPan.clearAnimation();
		// ��ͣ����
		MyPlayer.stopTheSong(MainActivity.this);

		super.onPause();
	}

	private Song loadStageSongInfo(int stageIndex) {
		Song song = new Song();

		String[] stage = Const.SONG_INFO[stageIndex];
		song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
		song.setSongName(stage[Const.INDEX_SONG_NAME]);

		return song;
	}

	/**
	 * ���ص�ǰ�ص�����
	 */
	private void initCurrentStageData() {
		// ��ȡ��ǰ�صĸ�����Ϣ
		mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);
		// ��ʼ����ѡ���
		mBtnSelectWords = initWordSelect();

		LayoutParams params = new LayoutParams(140, 140);
		// ���ԭ���Ĵ�
		mViewWordsContainer.removeAllViews();
		// �����µĴ𰸿�
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton,
					params);
		}
		// ��ʾ��ǰ�����浱ǰ�P��
		mCurrentStageView = (TextView) findViewById(R.id.text_current_stage);
		if (mCurrentStageView != null) {
			mCurrentStageView.setText((mCurrentStageIndex + 1) + "");
		}
		// �������
		mAllWords = initAllWord();
		// ��������- MyGridView
		mMyGridView.updateData(mAllWords);
	}

	/**
	 * ��ʼ����ѡ���ֿ�
	 */
	private ArrayList<WordButton> initAllWord() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();

		// ������д�ѡ����
		String[] words = generateWords();

		for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
			WordButton button = new WordButton();

			button.mWordString = words[i];

			data.add(button);
		}

		return data;
	}

	/**
	 * ��ʼ����ѡ�����ֿ�
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initWordSelect() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();

		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			View view = Util.getView(MainActivity.this,
					R.layout.self_ui_gridview_item);

			final WordButton holder = new WordButton();

			holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
			holder.mViewButton.setTextColor(Color.WHITE);
			holder.mViewButton.setText("");
			holder.mIsVisiable = false;

			holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
			holder.mViewButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					clearTheAnswer(holder);
				}
			});

			data.add(holder);
		}

		return data;
	}

	/**
	 * �������еĴ�ѡ����
	 * 
	 * @return
	 */
	private String[] generateWords() {
		Random random = new Random();

		String[] words = new String[MyGridView.COUNTS_WORDS];

		// �������
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			words[i] = mCurrentSong.getNameCharacters()[i] + "";
		}

		// ��ȡ������ֲ���������
		for (int i = mCurrentSong.getNameLength(); i < MyGridView.COUNTS_WORDS; i++) {
			words[i] = getRandomChar() + "";
		}

		// ��������˳�����ȴ�����Ԫ�������ѡȡһ�����һ��Ԫ�ؽ��н�����
		// Ȼ���ڵڶ���֮��ѡ��һ��Ԫ����ڶ���������֪�����һ��Ԫ�ء�
		// �����ܹ�ȷ��ÿ��Ԫ����ÿ��λ�õĸ��ʶ���1/n��
		for (int i = MyGridView.COUNTS_WORDS - 1; i >= 0; i--) {
			int index = random.nextInt(i + 1);

			String buf = words[index];
			words[index] = words[i];
			words[i] = buf;
		}

		return words;
	}

	/**
	 * ����������� http://www.cnblogs.com/skyivben/archive/2012/10/20/2732484.html
	 * 
	 * @return
	 */
	private char getRandomChar() {
		String str = "";
		int hightPos;
		int lowPos;

		Random random = new Random();

		hightPos = (176 + Math.abs(random.nextInt(39)));
		lowPos = (161 + Math.abs(random.nextInt(93)));

		byte[] b = new byte[2];
		b[0] = (Integer.valueOf(hightPos)).byteValue();
		b[1] = (Integer.valueOf(lowPos)).byteValue();

		try {
			str = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return str.charAt(0);
	}

	/**
	 * ����
	 * 
	 * @return
	 */
	private int checkTheAnswer() {
		// �ȼ�鳤��
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			// ����пյģ�˵���𰸻�������
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				return STATUS_ANSWER_LACK;
			}
		}

		// �����������������ȷ��
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			sb.append(mBtnSelectWords.get(i).mWordString);
		}

		return (sb.toString().equals(mCurrentSong.getSongName())) ? STATUS_ANSWER_RIGHT
				: STATUS_ANSWER_WRONG;
	}

	/**
	 * ������˸
	 */
	private void sparkTheWrods() {
		// ��ʱ�����
		TimerTask task = new TimerTask() {
			boolean mChange = false;
			int mSpardTimes = 0;

			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						if (++mSpardTimes > SPASH_TIMES) {
							return;
						}

						// ִ����˸�߼���������ʾ��ɫ�Ͱ�ɫ����
						for (int i = 0; i < mBtnSelectWords.size(); i++) {
							mBtnSelectWords.get(i).mViewButton
									.setTextColor(mChange ? Color.RED
											: Color.WHITE);
						}

						mChange = !mChange;
					}
				});
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, 1, 150);
	}

	/**
	 * �Զ�ѡ��һ����
	 */
	private void tipAnswer() {
		boolean tipWord = false;
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				// ���ݵ�ǰ�Ĵ𰸿�����ѡ���Ӧ�����ֲ�����
				onWordButtonClick(findIsAnswerWord(i));

				tipWord = true;

				// ���ٽ������
				if (!handleCoins(-getTipCoins())) {
					// ���������������ʾ�Ի���
					return;
				}
				break;
			}
		}

		// û���ҵ��������Ĵ�
		if (!tipWord) {
			// ��˸������ʾ�û�
			sparkTheWrods();
		}
	}

	/**
	 * ɾ������
	 */
	private void deleteOneWord() {
		// ���ٽ��
		if (!handleCoins(-getDeleteWordCoins())) {
			// ��Ҳ�������ʾ��ʾ�Ի���
			return;
		}

		// �����������Ӧ��WordButton����Ϊ���ɼ�
		setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);
	}

	/**
	 * �ҵ�һ�����Ǵ𰸵��ļ������ҵ�ǰ�ǿɼ���
	 * 
	 * @return
	 */
	private WordButton findNotAnswerWord() {
		Random random = new Random();
		WordButton buf = null;

		while (true) {
			int index = random.nextInt(MyGridView.COUNTS_WORDS);

			buf = mAllWords.get(index);

			if (buf.mIsVisiable && !isTheAnswerWord(buf)) {
				return buf;
			}
		}
	}

	/**
	 * �ҵ�һ��������
	 * 
	 * @param index
	 *            ��ǰ��Ҫ����𰸿������
	 * @return
	 */
	private WordButton findIsAnswerWord(int index) {
		WordButton buf = null;

		for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
			buf = mAllWords.get(i);

			if (buf.mWordString.equals(""
					+ mCurrentSong.getNameCharacters()[index])) {
				return buf;
			}
		}

		return null;
	}

	/**
	 * �ж�ĳ�������Ƿ�Ϊ��
	 * 
	 * @param word
	 * @return
	 */
	private boolean isTheAnswerWord(WordButton word) {
		boolean result = false;

		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			if (word.mWordString.equals(""
					+ mCurrentSong.getNameCharacters()[i])) {
				result = true;

				break;
			}
		}

		return result;
	}

	/**
	 * ���ӻ��߼���ָ�������Ľ��
	 * 
	 * @param data
	 * @return true ����/���ٳɹ���false ʧ��
	 */
	private boolean handleCoins(int data) {
		// �жϵ�ǰ�ܵĽ�������Ƿ�ɱ�����
		if (mCurrentCoins + data >= 0) {
			mCurrentCoins += data;

			mViewCurrentCoins.setText(mCurrentCoins + "");

			return true;
		} else {
			// ��Ҳ���
			return false;
		}
	}

	/**
	 * �������ļ����ȡɾ��������Ҫ�õĽ��
	 * 
	 * @return
	 */
	private int getDeleteWordCoins() {
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}

	/**
	 * �������ļ����ȡ��ʾ������Ҫ�õĽ��
	 * 
	 * @return
	 */
	private int getTipCoins() {
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}

	/**
	 * ����ɾ����ѡ�����¼�
	 */
	private void handleDeleteWord() {
		ImageButton button = (ImageButton) findViewById(R.id.btn_delete_word);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				deleteOneWord();
			}
		});

	}

	/**
	 * ������ʾ�����¼�
	 */
	private void handleTipAnswer() {
		ImageButton button = (ImageButton) findViewById(R.id.btn_tip_answer);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				tipAnswer();
			}
		});
	}

}
