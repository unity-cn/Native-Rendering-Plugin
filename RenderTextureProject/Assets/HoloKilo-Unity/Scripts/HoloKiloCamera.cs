using UnityEngine;
using System.Collections;

public class HoloKiloCamera : MonoBehaviour
{
	public bool updateCameraMatrix = true;
	public Light ambientLight;
	public Camera secondCamera;
	public GameObject[] trackedObjects;
	public int scaleRenderTexture = 1;
	
	private RenderTexture renderTexture;
	private Camera firstCamera;
	
	private int width =0;
	private int height =0;
	
	private AndroidJavaClass androidClass;
	private bool drawCardboard = false;
	
	void Start (){
		
		androidClass = new AndroidJavaClass("com.example.UnityRenderer");
		
		firstCamera = GetComponent<Camera>();
		
		
		if (secondCamera != null)
		{
			secondCamera.rect = new Rect(0.5f, 0, 0.5f, 1f);
		}
		
		Screen.sleepTimeout = SleepTimeout.NeverSleep;
	}
	
	AndroidJavaObject androidObject = null;
	void OnPreRender()
	{
		//if (androidObject != null && width != 0 && height!=0) {
		if (androidObject != null){
			if (androidObject.Call<bool> ("isStopped"))
				return;
			
			width = androidObject.Call<int> ("getScreenWidth") / scaleRenderTexture;
			height = androidObject.Call<int> ("getScreenHeight") / scaleRenderTexture;
			
			if (renderTexture != null){
				// Has screen size changed, then update rendertexture
				if (renderTexture.width != width || renderTexture.height != height) {
					// Discard previous rendertexture, make new one
					renderTexture.DiscardContents ();
					//Debug.Log ("Update with width: " + width + " height: " + height);
					logandroid("Update with width: " + width + " height: " + height);
					renderTexture = new RenderTexture (width, height, 24, RenderTextureFormat.ARGB32);
					
					// Set cameras textures
					firstCamera.targetTexture = renderTexture;
					if (secondCamera != null)
						secondCamera.targetTexture = renderTexture;
					
					float cameraVFov = 49f;//androidObject.Call<float> ("getSceneVFov");
					float cameraAspect = 1.3f;//androidObject.Call<float> ("getSceneAspect");
					
					//相机的视野，以度为单位。
					firstCamera.fieldOfView = cameraVFov;
					//长宽比（宽度除以高度）。
					firstCamera.aspect = cameraAspect;
					if (secondCamera != null) {
						secondCamera.fieldOfView = cameraVFov;
						secondCamera.aspect = cameraAspect;
					}
					
					androidObject.Call ("setUnityTextureID", renderTexture.GetNativeTexturePtr ().ToInt32 ());
					
					firstCamera.Render ();
					
					drawCardboard = false;//androidObject.Call<bool> ("hasCardboard");
					if (drawCardboard) {
						firstCamera.rect = new Rect (0, 0, 0.5f, 1f);
						
						// Be sure to call render here or will never update
						if (secondCamera != null) {
							secondCamera.enabled = true;
							secondCamera.Render ();
						}
					} else {
						if (secondCamera != null) {
							secondCamera.enabled = false;
						}
						firstCamera.rect = new Rect (0, 0, 1f, 1f);
					}
				}
			} else {
				logandroid("Create with width: " + width + " height: " + height);
				//Debug.Log ("Create with width: " + width + " height: " + height);
				renderTexture = new RenderTexture (width, height, 24, RenderTextureFormat.ARGB32);
				
				firstCamera.targetTexture = renderTexture;
				if (secondCamera != null)
					secondCamera.targetTexture = renderTexture;
				// Be sure to call render here or will never update
				firstCamera.Render ();
				if (secondCamera != null)
					secondCamera.Render ();
				
				float cameraVFov = 49f;//androidObject.Call<float> ("getSceneVFov");
				float cameraAspect = 1.3f;//androidObject.Call<float> ("getSceneAspect");
				
				firstCamera.fieldOfView = cameraVFov;
				firstCamera.aspect = cameraAspect;
				if (secondCamera != null) {
					secondCamera.fieldOfView = cameraVFov;
					secondCamera.aspect = cameraAspect;
				}
				
				androidObject.Call ("setUnityTextureID", renderTexture.GetNativeTexturePtr ().ToInt32 ());
				
				drawCardboard = false;//androidObject.Call<bool> ("hasCardboard");
				if (drawCardboard) {
					if (secondCamera != null) {
						secondCamera.enabled = true;
					}
					firstCamera.rect = new Rect (0, 0, 0.5f, 1f);
				} else {
					if (secondCamera != null) {
						secondCamera.enabled = false;
					}
					firstCamera.rect = new Rect (0, 0, 1f, 1f);
				}
			}
			
			
		} else {
			if (androidClass == null) {

			} else {
				androidObject = androidClass.CallStatic<AndroidJavaObject> ("getInstance");

			}
		}
	}
	
	void SetCameraTexture(string value)
	{
		Debug.Log("Load texture: " + value);
	}
	
	void logandroid(string text) {
		AndroidJavaClass log = new AndroidJavaClass ("android.util.Log");
		if(log != null)
			log.CallStatic<int>("d", "trigger Unity", text);
		//Debug.Log (text);
	}
}
