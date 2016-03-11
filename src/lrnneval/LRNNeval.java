/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lrnneval;

import discoverer.LiftedDataset;
import discoverer.Main;
import static discoverer.Main.parseArguments;
import discoverer.construction.network.LightTemplate;
import discoverer.global.Global;
import discoverer.global.Glogger;
import discoverer.grounding.evaluation.EvaluatorFast;
import discoverer.learning.Result;
import discoverer.learning.Results;
import discoverer.learning.Sample;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Gusta
 */
public class LRNNeval {

    static List<Sample> sampleStore;

    static enum Error {
        acc, mse, ace
    };
    static Error errorMeasure;

    private static List<Sample> loadSampleStore(String arg) {
        LiftedDataset data = LiftedDataset.loadDataset(arg);

        return data.sampleSplitter.samples;
    }

    public static void main(String[] args) {
        String gr = "avg";
        try {
            gr = args[0];
            if (gr.equals("max")) {
                sampleStore = loadSampleStore("examples2.ser");
                Global.uncompressedLambda = true;
            } else if (gr.equals("avg")) {
                sampleStore = loadSampleStore("examples.ser");
                Global.uncompressedLambda = false;
            }
        } catch (Exception ex) {
            System.out.println("invalid grounding argument, type only one of the two {avg,max}, setting default avg");
            sampleStore = loadSampleStore("examples.ser");
            Global.uncompressedLambda = false;
            gr = "avg";
        }
        try {
            String err = args[1];
            if (err.toLowerCase().equals("acc")) {
                errorMeasure = Error.acc;
            } else if (err.toLowerCase().equals("mse")) {
                errorMeasure = Error.mse;
            } else if (err.toLowerCase().equals("ace")) {
                errorMeasure = Error.ace;
            }
        } catch (Exception ex) {
            {
                System.out.println("invalid error measure argument, type only one of the three {acc,mse,ace}, setting default acc");
                errorMeasure = Error.acc;
            }
        }

        CommandLine cmd = parseArguments(new String[]{"-e", "n", "-r", "n", "-gr", gr});
        if (cmd == null) {
            return;
        }

        Main.setParameters(cmd);
        
        System.out.println("Successfully started the server with GROUNDING : " + gr + " and ERROR MEASURE : " + errorMeasure);

        //these are good weights for avg
        //String weights = "-0.9249754407553773,-0.9439484477283224,-1.4013522849823807,-0.9598978367657336,-0.4754250401146353,-1.7932462469657462,-1.2304729052748857,-1.2184932625697396,-2.829880202442235,0.4364598420280804,0.6376859875085558,-0.019436009946223222,0.498418782159168,0.6443247902888757,-0.3343447708895524,0.23897592644029247,-0.24124867520962723,-2.1524763687129775,3.877419358076544,3.3942225360835994,2.1543401552849883,3.0020089756435726,1.9822636950855645,2.4258443761454407,2.181033774000903,1.5080712098278846,0.47121142912505326,-1.1910685015297822,-0.3126868998695382,-0.02594375357781614,-0.32023216018349976,0.9625375043815988,-0.25436320486378555,0.013798031217081154,-0.20704101807950667,-1.1276740925422055,-0.1712157973654618,0.4096560129723409,0.2144032251321008,0.48510698466847313,1.2481870339553933,-0.003816236263597251,0.2032733438466816,0.024098089433525798,-0.955109609821293,1.5901764458269492,1.0463870261534285,1.2427149426506732,2.0581649541065326,1.153571431178797,0.8425985039864509,1.133175079871852,0.9850076240334972,0.436024182046061,0.7190442151869768,-0.6949744631672836,-1.3430293792031873,-0.8382932114774597,-0.8812384831575061,-0.10951752672865002,-1.2898627648305097,0.010040647478972918,1.9313004278675505,-2.0532819834427856,-1.611206101091242,-0.81093210318517,-1.6337332520419554,-0.9692838147240456,-0.3620702982252171,-1.4349421599753591,-0.31167693021378456,0.9745841925361296,0.07306098439755142,-0.6030481073042927,-0.9993957903357078,-0.8571877358520659,-0.8078594375954147,0.1504763167884256,-1.2788440840199118,0.25585818961727885,1.4121993837323314,-0.15931261321080603,0.6845874567920014,0.07711368629736551,0.5082716045370803,0.7147641776397765,-0.17118391143362202,0.18786949862321997,-0.2010442397965025,-2.1246434206632543,0.9619787830051358,0.8552839253309095,0.4921366886459156,0.8326639480031317,0.5225521938448122,0.5499426638803808,0.6399368251526891,-0.011260411597728287,-0.9982696782791805,2.2152522302494893,1.3342692111104495,1.3763426988243384,1.5451270874203673,0.9335145587844418,0.9111689060673769,0.8798703018494632,0.8152825208960369,0.40008489832827493,-0.11490326441913883,0.39577240982195694,0.15695201840742234,0.5602973811598536,0.43076973440896454,0.6710280725842391,0.39631968347280677,0.005348415687521984,-0.9937998838528831,0.6703828712900466,0.48985841758795706,0.47137395272743826,0.3011624570740844,0.2738978444853426,0.2811251964576915,0.1849097044696144,0.6823507200639191,-0.47835211698301305,1.4572377548221165,0.6950840960955923,0.6614817509928316,0.6673177143530127,0.6130158898402268,0.6017015176715418,0.6545298732530965,0.730886127190906,0.6562581148939964,-2.0011107137102786,-1.401624165690882,-1.1277718208361982,-1.6902473634511677,-0.9862354124304765,-0.3843835867526752,-0.8594391550479588,-0.08611846975069604,0.8581393589585085,-1.721914495038679,-1.0689598179490185,-0.3092157347739622,-0.8364101126846007,-0.5204360824511298,0.2389947535019853,-0.5252203675682054,0.30366104112661535,0.6159354284910474,-0.435872149297883,-0.6447337420043593,-0.23241168197119375,-0.5587083820758042,1.041243457257335,0.8817285003055622,-0.26701608310034963,1.3191728066043842,1.8496150463458647,3.386603227960955,2.577392932125604,2.0164687052326857,2.742386963987094,2.4759556449172653,1.5890798536880586,2.0797690499161274,1.3936996660368615,0.526266689832787,2.1494936444772117,1.3439789102692503,0.8974360954125638,1.5556876042678869,1.1133444680916704,0.7787072215871649,1.5272445935190495,0.9683117522027519,0.4508434743296963,3.148705453095126,2.1940503695982123,1.6170595825117984,1.9767515040098467,1.6783036801449804,1.3824772146737834,1.6598092042514219,1.310974231133748,1.4049921979750997,1.633755224364549,1.0629489030486772,0.9762989330542741,1.4533820060237872,0.9094976190919496,0.8464727321127448,0.9296645298141584,1.0011717812536227,0.5516417044666925,1.1266215110473177,1.6538201367889955,1.1374806460255649,0.6752233307760405,0.7673516544586589,0.5878797298215289,0.6680993674487837,0.7634037525184827,0.6744098568932007,2.748531043995524,1.8408619985658856,1.3041882579216209,1.5087537467242018,1.127296144228879,1.3174403795296104,1.1867323878207339,1.1961429873938458,1.425842767060974,0.04768812897887412,-0.8844077695541581,-0.8311648584366699,-0.6635270206565486,-0.7873155311120089,0.08889868662824807,-1.3441739029149733,-0.050017920940007315,1.233200262847812,-0.5528033162942427,-0.7129326557932142,-0.2820925617554051,-0.5532163889953494,0.21697816891814925,1.061033810417074,-0.06640879293318157,2.0138602000138315,1.9703202786758967,1.4211571403952072,0.613376271567003,0.4646748603491338,0.2112926715157992,0.9855046302698077,1.8168885366878345,1.001503531199859,1.8063307278112284,3.3313453239347925,-2.3264743436987834,4.793842035562759,-8.641858080635599,3.836800525787465,2.034508221817777,-0.024755656183946692,2.115792520622966,0.9475720542214578,-5.364895256471904,2.0053737131748597,0.31496858344002393,0.11672463799316865,0.9552892680304245,3.487270960365534,2.022957705129027,-0.29188494657779257,0.06967012536285544,0.177689795616857,0.9665815520741936,-1.9184908008554555,12.745432090378438,-5.483594167246502,0.047704634782013824,0.6997784542823663,0.3662062683804299,-8.660009433642534,-1.5280111082445091,-2.4539552383839633,5.429591069430501,0.26143441249619165,0.9175521842932546,0.48173014028291994,-2.177203686590361,-0.09722806982236633,0.9945521821911535,-4.87028883165427,26.12253918706823,-12.2710763277996,-2.8652537670975398,0.36691567605104874,-7.8600371451070705,-0.5910491774723641,2.450029469879857,0.39375208578267795,-0.708324054555089,-6.8493116886415315,-0.33024817020006175,-0.5716524813846768,0.11638463772401231,0.011864989176039842,0.10045773645126843,0.7110906165522497,-1.507019305206718,0.21317675999595487,0.0879114052101766,0.3694790134693644,0.07609352336621988,0.2499891764164648,0.0029945561784234798,3.6815634891695246,0.06125980504647721,0.2755221624994224,0.18532411056350817,0.02521575478409943";
        //evaluate(weights);
    }

    public static double evaluate(String line) {
        String[] split = line.split("[\\t\\s ,]+");
        double[] weights = new double[split.length];
        double ress = -1;
        try {
            System.out.println("recieved line : " + line);
            System.out.println("vector length : " + split.length);
            for (int i = 0; i < weights.length; i++) {
                weights[i] = Double.parseDouble(split[i].trim());
            }
        } catch (Exception e) {
            System.out.println("problem evaluating");
        }

        switch (errorMeasure) {
            case acc:
                ress = evaluateAcc(weights);
                break;
            case mse:
                ress = evaluateMSE(weights);
                break;
            case ace:
                ress = evaluateACE(weights);
                break;
            default:
                throw new AssertionError();
        }
        return ress;
    }

    private static double evaluateAcc(double[] weights) {
        double ress = -1;
        Results res = new Results();
        for (Sample sam : sampleStore) {
            sam.neuralNetwork.outputNeuron.outputValue = EvaluatorFast.evaluateFast(sam.neuralNetwork, weights);
            //System.out.println(sam.neuralNetwork.outputNeuron.outputValue + " : " + sam.targetValue);
            res.add(new Result(sam.neuralNetwork.outputNeuron.outputValue, sam.targetValue));
        }
        ress = res.getLearningError();
        Glogger.process("All Ground Networks Evaluation : acc error " + ress + " (maj: " + res.getMajorityClass() + ")" + " (disp: " + res.getDispersion() + ")");
        return ress;
    }

    private static double evaluateMSE(double[] weights) {
        double sum = 0;
        Results res = new Results();
        for (Sample sam : sampleStore) {
            sam.neuralNetwork.outputNeuron.outputValue = EvaluatorFast.evaluateFast(sam.neuralNetwork, weights);
            //System.out.println(sam.neuralNetwork.outputNeuron.outputValue + " : " + sam.targetValue);
            res.add(new Result(sam.neuralNetwork.outputNeuron.outputValue, sam.targetValue));

            sum += (sam.neuralNetwork.outputNeuron.outputValue - sam.targetValue) * (sam.neuralNetwork.outputNeuron.outputValue - sam.targetValue);
        }
        sum /= sampleStore.size();  //MSE
        Glogger.process("All Ground Networks Evaluation : mean square error " + sum + ", acc error " + res.getLearningError() + " (maj: " + res.getMajorityClass() + ")" + " (disp: " + res.getDispersion() + ")");
        return sum;
    }

    private static double evaluateACE(double[] weights) {
        double sum = 0;
        Results res = new Results();
        for (Sample sam : sampleStore) {
            sam.neuralNetwork.outputNeuron.outputValue = EvaluatorFast.evaluateFast(sam.neuralNetwork, weights);
            //System.out.println(sam.neuralNetwork.outputNeuron.outputValue + " : " + sam.targetValue);
            res.add(new Result(sam.neuralNetwork.outputNeuron.outputValue, sam.targetValue));

            sum += -1 * (sam.targetValue * Math.log(sam.neuralNetwork.outputNeuron.outputValue) + (1 - sam.targetValue) * Math.log(1 - sam.neuralNetwork.outputNeuron.outputValue));
        }
        sum /= sampleStore.size();  //average cross-entropy
        Glogger.process("All Ground Networks Evaluation : average crossentropy error " + sum + ", acc error " + res.getLearningError() + " (maj: " + res.getMajorityClass() + ")" + " (disp: " + res.getDispersion() + ")");
        return sum;
    }
}
