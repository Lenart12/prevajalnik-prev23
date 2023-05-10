package prev23.phase.regall;

import org.antlr.runtime.tree.Tree;
import prev23.Compiler;
import prev23.common.report.Report;
import prev23.data.mem.MemTemp;
import prev23.phase.asmgen.AsmGen;

import java.io.Serial;
import java.math.BigInteger;
import java.util.*;
import java.util.zip.Deflater;

public class AllocationGraph {

    private static class TempNode implements Comparable<TempNode> {
        final MemTemp temp;
        int assigned_register = -1;
        final TreeSet<TempNode> neighbours = new TreeSet<>();
        boolean simplified = false;
        boolean spilled = false;

        public TempNode(MemTemp temp) {
            this.temp = temp;
        }

        public void add_edge(TempNode o) {
            if (o == null) throw new Report.InternalError();
            neighbours.add(o);
        }

        public int get_degree() {
            int s = 0;
            for (var n : neighbours) if (!(n.simplified || n.spilled)) s++;
            return s;
        }

        @Override
        public String toString() {
            var sb = new StringBuilder();
            sb.append("T");
            sb.append(temp.temp);
            if (assigned_register != -1) {
                sb.append(" $");
                sb.append(assigned_register);
            }
            if (spilled) {
                sb.append(" Spilled");
            }
            if (simplified) {
                sb.append(" Simplified");
            }
            return sb.toString();
        }

        public boolean try_allocate() {
            if (assigned_register != -1) return true;

            var assigned_neighbours = new TreeSet<TempNode>(Comparator.comparingInt(n -> n.assigned_register));
            for (var n : neighbours) {
                if (n.simplified || n.spilled || n.assigned_register == -1) continue;
                assigned_neighbours.add(n);
            }

            if (assigned_neighbours.isEmpty()) {
                assigned_register = 0;
                simplified = spilled = false;
                return true;
            } else if (assigned_neighbours.size() >= Compiler.num_regs) {
                return false;
            }

            // 0 0 1 2 2 4 5
            var my_register = 0;
            for (var node : assigned_neighbours) {
                var n = node.assigned_register;
                if (my_register == n || my_register + 1 == n) {
                    my_register = n;
                    continue;
                }
                break;
            }

            assigned_register = my_register + 1;
            simplified = spilled = false;
            return true;
        }

        @Override
        public int compareTo(TempNode o) {
            if (o == null) return -1;
            return Long.compare(temp.temp, o.temp.temp);
        }
    }

    private final Vector<TempNode> temp_nodes = new Vector<>();

    private final HashSet<MemTemp> spilled_nodes = new HashSet<>();

    public TempNode get_node(MemTemp t) {
        var idx = (int)t.temp;

        if (temp_nodes.size() <= idx) {
            temp_nodes.setSize(idx + 1);
        }

        var node = temp_nodes.get(idx);

        if (node == null) {
            node = new TempNode(t);
            temp_nodes.set(idx, node);
        }

        return node;
    }

    public void set_edge(MemTemp t1, MemTemp t2) {
        var n1 = get_node(t1);
        var n2 = get_node(t2);

        n1.add_edge(n2);
        n2.add_edge(n1);
    }

    public void set_all_pairs(HashSet<MemTemp> temps, MemTemp ignored_FP) {
        if (temps.isEmpty()) return;

        for (var temp : temps) {
            if (temp != ignored_FP) get_node(temp);
        }

        if (temps.size() == 1) {
            return;
        }

        var _temps = temps.stream().toList();

        for (int i = 0; i < _temps.size(); i++) {
            if (_temps.get(i) == ignored_FP) continue;

            for (int j = i + 1; j < _temps.size(); j++) {
                if (_temps.get(j) == ignored_FP) continue;

                set_edge(_temps.get(i), _temps.get(j));
            }
        }
    }

    public boolean try_allocate() {
        var allocation_stack = new LinkedList<TempNode>();
        spilled_nodes.clear();

        var processing_nodes = new TreeSet<TempNode>();
        for (var node : temp_nodes) if (node != null) processing_nodes.add(node);

        while (!processing_nodes.isEmpty()) {
            var did_simplify = false;
            var can_spill = false;
            do {
                did_simplify = false;
                can_spill = false;

                var it = processing_nodes.iterator();
                while (it.hasNext()) {
                    var node = it.next();

                    if (node.get_degree() < Compiler.num_regs) {
                        node.simplified = true;
                        did_simplify = true;
                        allocation_stack.push(node);
                        it.remove();
                    } else {
                        can_spill = true;
                    }
                }
            } while (did_simplify);

            if (can_spill) {
                var it = processing_nodes.iterator();
                while (it.hasNext()) {
                    var node = it.next();
                    if (node.get_degree() >= Compiler.num_regs) {
                        node.spilled = true;
                        allocation_stack.push(node);
                        it.remove();
                        break;
                    }
                }
            }
        }

        while (!allocation_stack.isEmpty()) {
            var node = allocation_stack.pop();

            if (!node.try_allocate()) {
                spilled_nodes.add(node.temp);
            }
        }

        return spilled_nodes.isEmpty();
    }

    public HashSet<MemTemp> get_spilled_nodes() {
        return spilled_nodes;
    }

    public void fill_temp_to_reg(HashMap<MemTemp, Integer> temp_to_reg) {
        for (var node : temp_nodes) {
            if (node == null) continue;
            temp_to_reg.put(node.temp, node.assigned_register);
        }
    }


    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("@startuml\n");
        sb.append("graph G {\n");

        for (var node : temp_nodes) {
            if (node == null) continue;
            if (node.neighbours.isEmpty()) {
                sb.append(String.format("\"%s\";", node));
            } else {
                for (var n : node.neighbours) {
                    if (n.compareTo(node) > 0) {
                        sb.append(String.format("\"%s\" -- \"%s\";\n", node, n));
                    }
                }
            }
        }

        sb.append("}\n");
        sb.append("@enduml\n");

        byte[] input = sb.toString().getBytes();

        // Compress the bytes
        byte[] output = new byte[4096];
        Deflater compressor = new Deflater();
        compressor.setInput(input);
        compressor.finish();
        int compressed_length = compressor.deflate(output);
        compressor.end();

        final var base64_original = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        final var plantuml_mapping = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_";
        var mapping = new Vector<Character>();
        mapping.setSize(256);
        for (int i = 0; i < base64_original.length(); i++) {
            mapping.set(base64_original.codePointAt(i), plantuml_mapping.charAt(i));
        }

        var b64_text = Base64.getEncoder().encodeToString(Arrays.copyOfRange(output, 0, compressed_length));
        sb = new StringBuilder();

        for (var c : b64_text.toCharArray()) {
            if (mapping.get(c) == null) {
                sb.append(c);
                continue;
            }
            sb.append(mapping.get(c));
        }

        return String.format("https://www.plantuml.com/plantuml/png/~1%s", sb);
    }
}
