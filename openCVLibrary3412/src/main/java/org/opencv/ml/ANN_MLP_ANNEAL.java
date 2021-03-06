//
// This file is auto-generated. Please don't modify it!
//
package org.opencv.ml;

// C++: class ANN_MLP_ANNEAL

/**
 * Artificial Neural Networks - Multi-Layer Perceptrons.
 * <p>
 * SEE: REF: ml_intro_ann
 */
public class ANN_MLP_ANNEAL extends ANN_MLP {

    protected ANN_MLP_ANNEAL(long addr) {
        super(addr);
    }

    // internal usage only
    public static ANN_MLP_ANNEAL __fromPtr__(long addr) {
        return new ANN_MLP_ANNEAL(addr);
    }

    //
    // C++:  double cv::ml::ANN_MLP_ANNEAL::getAnnealCoolingRatio()
    //

    // C++:  double cv::ml::ANN_MLP_ANNEAL::getAnnealCoolingRatio()
    private static native double getAnnealCoolingRatio_0(long nativeObj);


    //
    // C++:  double cv::ml::ANN_MLP_ANNEAL::getAnnealFinalT()
    //

    // C++:  double cv::ml::ANN_MLP_ANNEAL::getAnnealFinalT()
    private static native double getAnnealFinalT_0(long nativeObj);


    //
    // C++:  double cv::ml::ANN_MLP_ANNEAL::getAnnealInitialT()
    //

    // C++:  double cv::ml::ANN_MLP_ANNEAL::getAnnealInitialT()
    private static native double getAnnealInitialT_0(long nativeObj);


    //
    // C++:  int cv::ml::ANN_MLP_ANNEAL::getAnnealItePerStep()
    //

    // C++:  int cv::ml::ANN_MLP_ANNEAL::getAnnealItePerStep()
    private static native int getAnnealItePerStep_0(long nativeObj);


    //
    // C++:  void cv::ml::ANN_MLP_ANNEAL::setAnnealCoolingRatio(double val)
    //

    // C++:  void cv::ml::ANN_MLP_ANNEAL::setAnnealCoolingRatio(double val)
    private static native void setAnnealCoolingRatio_0(long nativeObj, double val);


    //
    // C++:  void cv::ml::ANN_MLP_ANNEAL::setAnnealFinalT(double val)
    //

    // C++:  void cv::ml::ANN_MLP_ANNEAL::setAnnealFinalT(double val)
    private static native void setAnnealFinalT_0(long nativeObj, double val);


    //
    // C++:  void cv::ml::ANN_MLP_ANNEAL::setAnnealInitialT(double val)
    //

    // C++:  void cv::ml::ANN_MLP_ANNEAL::setAnnealInitialT(double val)
    private static native void setAnnealInitialT_0(long nativeObj, double val);


    //
    // C++:  void cv::ml::ANN_MLP_ANNEAL::setAnnealItePerStep(int val)
    //

    // C++:  void cv::ml::ANN_MLP_ANNEAL::setAnnealItePerStep(int val)
    private static native void setAnnealItePerStep_0(long nativeObj, int val);

    // native support for java finalize()
    private static native void delete(long nativeObj);

    /**
     * SEE: setAnnealCoolingRatio
     *
     * @return automatically generated
     */
    public double getAnnealCoolingRatio() {
        return getAnnealCoolingRatio_0(nativeObj);
    }

    /**
     * getAnnealCoolingRatio SEE: getAnnealCoolingRatio
     *
     * @param val automatically generated
     */
    public void setAnnealCoolingRatio(double val) {
        setAnnealCoolingRatio_0(nativeObj, val);
    }

    /**
     * SEE: setAnnealFinalT
     *
     * @return automatically generated
     */
    public double getAnnealFinalT() {
        return getAnnealFinalT_0(nativeObj);
    }

    /**
     * getAnnealFinalT SEE: getAnnealFinalT
     *
     * @param val automatically generated
     */
    public void setAnnealFinalT(double val) {
        setAnnealFinalT_0(nativeObj, val);
    }

    /**
     * SEE: setAnnealInitialT
     *
     * @return automatically generated
     */
    public double getAnnealInitialT() {
        return getAnnealInitialT_0(nativeObj);
    }

    /**
     * getAnnealInitialT SEE: getAnnealInitialT
     *
     * @param val automatically generated
     */
    public void setAnnealInitialT(double val) {
        setAnnealInitialT_0(nativeObj, val);
    }

    /**
     * SEE: setAnnealItePerStep
     *
     * @return automatically generated
     */
    public int getAnnealItePerStep() {
        return getAnnealItePerStep_0(nativeObj);
    }

    /**
     * getAnnealItePerStep SEE: getAnnealItePerStep
     *
     * @param val automatically generated
     */
    public void setAnnealItePerStep(int val) {
        setAnnealItePerStep_0(nativeObj, val);
    }

    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }

}
