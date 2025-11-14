{
  description = "SwingInput dev flake";
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
  inputs.flake-utils.url = "github:numtide/flake-utils";

  outputs = {
    nixpkgs,
    flake-utils,
    ...
  }:
    flake-utils.lib.eachDefaultSystem (
      system: let
        pkgs = import nixpkgs {
          inherit system;
        };
      in {
        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [jdk25];
          shellHook = ''
            clear
            echo "SwingInput dev env activated" | ${pkgs.lolcat}/bin/lolcat
          '';
        };
      }
    );
}
