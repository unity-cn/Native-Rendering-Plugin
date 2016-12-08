using UnityEngine;
using System.Collections;

public class Test : MonoBehaviour 
{

	//反射调用android的方法
	private AndroidJavaClass androidClass;
	AndroidJavaObject androidObject = null;

	//Android 端屏幕宽高
	private int width;
	private int height;

	//以textture的方式将Unity当前帧传递到Android端
	private RenderTexture renderTexture;

	// 主camera
	private Camera firstCamera;

	//请输入一个字符串
	private string stringToEdit = "Please enter a string";

	void Update () 
	{

	}

	void OnGUI()
	{


	}
	//注解2
	void messgae(string str)
	{
		stringToEdit = str;
		Debug.Log(stringToEdit);
	}


	void Start() {
		logandroid ("call new MyGLRenderer");
		androidClass = new AndroidJavaClass("com.xys.MyGLRenderer");


		firstCamera = GetComponent<Camera>();
	}

	void OnPreRender() {
		if (androidObject != null) {

			width = androidObject.Call<int> ("getScreenWidth");
			height = androidObject.Call<int> ("getScreenHeight");

			if (renderTexture != null) {
				if (renderTexture.width != width || renderTexture.height != height) {
					// Discard previous rendertexture, make new one
					renderTexture.DiscardContents ();
					renderTexture = null;
				}
			} 

			if (renderTexture == null) {
				renderTexture = new RenderTexture (width, height, 24, RenderTextureFormat.ARGB32);
				firstCamera.targetTexture = renderTexture;

				//这个方法一定要调用，否则不会刷新
				firstCamera.Render ();

				logandroid ("call setUnityTextureID");
				androidObject.Call ("setUnityTextureID", renderTexture.GetNativeTexturePtr ().ToInt32 ());

			}



		} else {
			logandroid ("call getInstance");
			androidObject = androidClass.CallStatic<AndroidJavaObject> ("getInstance");
		}

	}

	void logandroid(string text) {
		AndroidJavaClass log = new AndroidJavaClass ("android.util.Log");
		log.CallStatic<int>("i","zxyUnity",text);
		print (text);
	}

}
