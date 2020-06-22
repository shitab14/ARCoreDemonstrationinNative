package com.mr_mir.arcoredemonstrationinnative

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.BaseArFragment
import com.google.ar.sceneform.ux.TransformableNode


class MainActivity : AppCompatActivity(), BaseArFragment.OnTapArPlaneListener {

    var arFragment: ArFragment? = null
    var modelRenderable: ModelRenderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(isARCoreInstalled()) {
            //Initialize Fragment
            arFragment = supportFragmentManager.findFragmentById(R.id.fragment) as ArFragment
            setUpModel()
            setUpPlane()
        } else {
            Toast.makeText(this, "Oops, ARCore Not Installed.", Toast.LENGTH_LONG).show()
        }


    }

    private fun isARCoreInstalled(): Boolean {
        return try {
            packageManager.getPackageInfo("com.google.ar.core", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }


    //Setting up Model
    private fun setUpModel() {
        try {
            ModelRenderable.builder()
                .setSource(this, R.raw.rocket)
                .build().complete(modelRenderable)
                /*.thenAccept(modelRenderable)
                .exceptionally {
                    Toast.makeText(this, "Failed to load.", Toast.LENGTH_LONG).show()
                    throw Throwable("Failed to load")
                }*/
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load.", Toast.LENGTH_LONG).show()
        }
    }

    //Setting up Plane Surface
    private fun setUpPlane() {
        arFragment?.setOnTapArPlaneListener(this)
    }

    override fun onTapPlane(hitResult: HitResult?, plane: Plane?, motionEvent: MotionEvent?) {
        val anchor: Anchor = hitResult!!.createAnchor()
        val anchorNode: AnchorNode = AnchorNode(anchor)
        anchorNode.setParent(arFragment?.arSceneView?.scene)

        //CREATE MODEL
        val node: TransformableNode = TransformableNode(arFragment?.transformationSystem)
        node.setParent(anchorNode)
        node.renderable = modelRenderable as Renderable
        node.select()
    }
}